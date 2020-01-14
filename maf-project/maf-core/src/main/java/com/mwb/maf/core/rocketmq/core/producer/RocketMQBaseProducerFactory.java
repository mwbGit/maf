package com.mwb.maf.core.rocketmq.core.producer;

import com.mwb.maf.core.rocketmq.core.RocketMQBaseFactory;

public class RocketMQBaseProducerFactory extends RocketMQBaseFactory {

    public static final String PREFIX_APP_ROCKET_PRODUCER = "maf.mq.rocketmq.producer";

    protected RocketMQProducerConfig createRocketProducerConfig() {
        RocketMQProducerConfig config = new RocketMQProducerConfig();
        return config;
    }

    protected String getRocketProducerPrefix() {
        return PREFIX_APP_ROCKET_PRODUCER;
    }

}
