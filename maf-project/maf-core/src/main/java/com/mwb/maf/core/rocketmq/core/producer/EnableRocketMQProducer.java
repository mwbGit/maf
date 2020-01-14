package com.mwb.maf.core.rocketmq.core.producer;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(EnableRocketMQProducers.class)
@Import(RocketMQProducerRegistrar.class)
public @interface EnableRocketMQProducer {

    String namespace() default "default";

    String producerGroup() default "";

    boolean autoStart() default false;

}