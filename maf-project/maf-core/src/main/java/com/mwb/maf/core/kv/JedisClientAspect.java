package com.mwb.maf.core.kv;

import com.mwb.maf.core.metrics.LatencyProfiler;
import com.mwb.maf.core.metrics.LatencyStat;
import com.mwb.maf.core.metrics.MonitorConfig;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;

@Aspect
public class JedisClientAspect {

    private static final LatencyStat JEDIS_STAT = LatencyProfiler.Builder.build()
            .tag("jedis")
            .name("jedis")
            .defineLabels("operation", "namespace")
            .buckets(
                    0.0001, 0.0005, 0.0009,
                    0.001, 0.003, 0.005, 0.007, 0.009,
                    0.01, 0.03, 0.05, 0.07, 0.09,
                    0.1, 0.3, 0.5, 0.7, 0.9,
                    1, 3, 5)
            .create();

    @Autowired
    private MonitorConfig monitorConfig;

    @Pointcut(
            "execution(* com.mwb.maf.core.kv.JedisClient.*(..)) &&" +
                    "!execution(* com.mwb.maf.core.kv.JedisClient.getAddress(..)) &&" +
                    "!execution(* com.mwb.maf.core.kv.JedisClient.getPort(..)) &&" +
                    "!execution(* com.mwb.maf.core.kv.JedisClient.getJedisPoolConfig(..)) &&" +
                    "!execution(* com.mwb.maf.core.kv.JedisClient.getDriver(..)) &&" +
                    "!execution(* com.mwb.maf.core.kv.JedisClient.getJedisPool(..)) &&" +
                    "!execution(* com.mwb.maf.core.kv.JedisClient.JedisClient(..))"
    )
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!monitorConfig.isEnableJedisProfile()) {
            return joinPoint.proceed();
        }
        String namespace = "";
        if (joinPoint.getTarget() instanceof JedisClient) {
            namespace = ((JedisClient) joinPoint.getTarget()).getNamespace();
        }

        final String methodName = joinPoint.getSignature().getName();
        final LatencyStat.Timer timer = JEDIS_STAT.startTimer(methodName, namespace);
        try {
            JEDIS_STAT.inc(methodName, namespace);
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            JEDIS_STAT.error(methodName, namespace);
            throw throwable;
        } finally {
            timer.observeDuration();
            JEDIS_STAT.dec(methodName, namespace);
        }
    }

}
