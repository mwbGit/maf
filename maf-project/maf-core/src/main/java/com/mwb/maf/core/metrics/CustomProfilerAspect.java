package com.mwb.maf.core.metrics;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;

@Aspect
public class CustomProfilerAspect {
    @Autowired
    private MonitorConfig monitorConfig;

    @Pointcut("@annotation(com.mwb.maf.core.metrics.Profile)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!monitorConfig.isEnableCustomProfile()) {
            return joinPoint.proceed();
        }
        final String metrics = joinPoint.getSignature().toShortString();
        final String category = joinPoint.getSignature().getDeclaringType().getSimpleName();
        final LatencyStat.Timer timer = CustomProfiler.CUSTOMIZED_STAT.startTimer(metrics, category);
        try {
            CustomProfiler.CUSTOMIZED_STAT.inc(metrics, category);
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            CustomProfiler.CUSTOMIZED_STAT.error(metrics, category);
            throw throwable;
        } finally {
            timer.observeDuration();
            CustomProfiler.CUSTOMIZED_STAT.dec(metrics, category);
        }
    }
}
