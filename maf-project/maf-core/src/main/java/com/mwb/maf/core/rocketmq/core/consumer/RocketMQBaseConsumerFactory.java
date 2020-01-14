package com.mwb.maf.core.rocketmq.core.consumer;

import com.mwb.maf.core.rocketmq.core.RocketMQBaseFactory;

public class RocketMQBaseConsumerFactory extends RocketMQBaseFactory {

    public static final String PREFIX_APP_ROCKET_CONSUMER = "maf.mq.rocketmq.consumer";

    protected RocketMQConsumerConfig createRocketConsumerConfig() {
        RocketMQConsumerConfig config = new RocketMQConsumerConfig();
        return config;
    }

    protected String getRocketConsumerPrefix() {
        return PREFIX_APP_ROCKET_CONSUMER;
    }

}
