package com.mwb.maf.core.rocketmq.core;

public abstract class RocketMQBaseFactory {

    public static final String PREFIX_APP_ROCKET = "maf.mq.rocketmq";

    protected String getRocketPrefix() {
        return PREFIX_APP_ROCKET;
    }
}
