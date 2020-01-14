package com.mwb.maf.core.rocketmq.core;

import com.mwb.maf.core.rocketmq.core.producer.RocketMQProducerAspect;
import com.mwb.maf.core.rocketmq.producer.RocketMQSimpleProducer;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

@ConditionalOnClass({
        RocketMQSimpleProducer.class,
        DefaultMQProducer.class
})
public class RocketMQAutoConfiguration {

    @Bean
    public RocketMQProducerAspect rocketMQProducerAspect() {
        return new RocketMQProducerAspect();
    }

}
