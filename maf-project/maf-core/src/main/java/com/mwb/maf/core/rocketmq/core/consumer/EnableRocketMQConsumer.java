package com.mwb.maf.core.rocketmq.core.consumer;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(EnableRocketMQConsumers.class)
@Import(RocketMQConsumerRegistrar.class)
public @interface EnableRocketMQConsumer {

    String namespace() default "default";

}