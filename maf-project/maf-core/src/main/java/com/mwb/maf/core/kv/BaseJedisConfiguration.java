package com.mwb.maf.core.kv;

import redis.clients.jedis.JedisPoolConfig;

public class BaseJedisConfiguration {
    public static final String PREFIX_APP_JEDIS = "app.jedis";
    public static final String PREFIX_APP_JEDIS_CLUSTER = "app.jedis-cluster";

    protected JedisPoolConfig createJedisPoolConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(300);
        config.setMaxIdle(10);
        config.setMinIdle(5);
        config.setMaxWaitMillis(6000);
        config.setTestOnBorrow(false);
        config.setTestOnReturn(false);
        config.setTestWhileIdle(true);
        config.setTestOnCreate(false);
        return config;
    }

}
