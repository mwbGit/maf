package com.mwb.maf.core.kv;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class CJedisPool extends JedisPool {

    public CJedisPool(JedisPoolConfig jedisPoolConfig, String address, int port) {
        super(jedisPoolConfig, address, port);
    }

    public void warmUp() {
        try {
            internalPool.preparePool();
        } catch (Exception e) {

        }
    }
}
