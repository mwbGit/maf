package com.mwb.maf.core.rocketmq.core.producer;

import com.mwb.maf.core.metrics.LatencyProfiler;
import com.mwb.maf.core.metrics.LatencyStat;
import com.mwb.maf.core.metrics.MonitorConfig;
import com.mwb.maf.core.rocketmq.producer.RocketMQSimpleProducer;
import com.mwb.maf.core.util.PIDUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

@Aspect
public class RocketMQProducerAspect {

    private static final LatencyStat ROCKETMQ_PRODUCER_SEND_COUNT_STAT = LatencyProfiler.Builder.build().tag("rocketmq")
            .name("rocketmq_producer_send").labelNames("topic", "producerGroup", "clazz", "pid")
            .buckets(0.000, 0.001, 0.002, 0.003, 0.004, 0.005, 0.006, 0.007, 0.008, 0.009, 0.010, 0.02, 0.05, 0.1, 0.3,
                    0.5, 0.7, 0.9, 1, 3, 5, 10, 20, 30, 100, 1000, 1000 * 10)
            .create();

    @Autowired
    private MonitorConfig monitorConfig;

    @Pointcut("execution(* com.mwb.maf.core.rocketmq.producer.RocketMQSimpleProducer.send(..))")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!monitorConfig.isEnableRocketMQProducerProfile()) {
            return joinPoint.proceed();
        }

        String topic = "";
        if (joinPoint.getTarget() instanceof RocketMQSimpleProducer) {
            Object[] objArray = joinPoint.getArgs();
            if (objArray != null && objArray.length >= 1 && objArray[0] != null) {
                topic = joinPoint.getArgs()[0].toString();
            }
        } else {
            return joinPoint.proceed();
        }

        RocketMQSimpleProducer target = (RocketMQSimpleProducer) (joinPoint.getTarget());
        // String operation = joinPoint.getSignature().getName();
        long processStartTime = System.currentTimeMillis();

        try {
            ROCKETMQ_PRODUCER_SEND_COUNT_STAT.inc(topic, target.getProducerGroup(), target.getClass().getName(),
                    PIDUtil.getProcessID());
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            ROCKETMQ_PRODUCER_SEND_COUNT_STAT.error(topic, target.getProducerGroup(), target.getClass().getName(),
                    PIDUtil.getProcessID());
            throw throwable;
        } finally {
            ROCKETMQ_PRODUCER_SEND_COUNT_STAT.observe((System.currentTimeMillis() - processStartTime),
                    TimeUnit.MILLISECONDS, topic, target.getProducerGroup(), target.getClass().getName(),
                    PIDUtil.getProcessID());
            ROCKETMQ_PRODUCER_SEND_COUNT_STAT.dec(topic, target.getProducerGroup(), target.getClass().getName(),
                    PIDUtil.getProcessID());
        }
    }

}
