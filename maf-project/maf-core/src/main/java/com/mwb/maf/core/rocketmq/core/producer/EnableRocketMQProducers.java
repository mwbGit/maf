package com.mwb.maf.core.rocketmq.core.producer;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(RocketMQProducerRegistrar.class)
public @interface EnableRocketMQProducers {

    EnableRocketMQProducer[] value();

}