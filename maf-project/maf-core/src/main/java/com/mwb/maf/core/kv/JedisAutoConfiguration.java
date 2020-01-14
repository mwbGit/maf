package com.mwb.maf.core.kv;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@ConditionalOnClass({
        Jedis.class,
        JedisPool.class,
        JedisPoolConfig.class
})
public class JedisAutoConfiguration {

    @Bean
    public JedisClientAspect jedisClientAspect() {
        return new JedisClientAspect();
    }


}
