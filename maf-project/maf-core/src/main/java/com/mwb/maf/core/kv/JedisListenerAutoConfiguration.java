package com.mwb.maf.core.kv;

import com.mwb.maf.core.AbstractConfigPrintSpringListener;
import com.mwb.maf.core.logging.Loggers;
import com.mwb.maf.core.util.LogUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Map;

@ConditionalOnClass({
        Jedis.class,
        JedisPool.class,
        JedisPoolConfig.class
})
public class JedisListenerAutoConfiguration {
    private static final Logger logger = Loggers.getFrameworkLogger();

    @Bean
    public HealthIndicator redisHealthIndicator() {
        return () -> Health.up().build();
    }

    @Bean
    public ApplicationListener<ApplicationEvent> jedisConfigListener() {
        return new AbstractConfigPrintSpringListener<JedisClient>() {
            @Override
            protected Class<JedisClient> getConfigClass() {
                return JedisClient.class;
            }

            @Override
            protected String getModuleName() {
                return "Jedis Standalone";
            }

            @Override
            protected void logConfigInfo(Map.Entry<String, JedisClient> entry) {
                if (entry == null)
                    return;
                JedisClient jedisClient = entry.getValue();

                JedisPoolConfig poolConfig = jedisClient.getJedisPoolConfig();
                LogUtils.putContextColumn1("config");
                LogUtils.putContextColumn2("jedis-std:" +
                        StringUtils.substringBefore(entry.getKey(), JedisClient.class.getSimpleName()) +
                        ":" + DateTime.now().toString("yyyyMMddHHmmss"));
                logger.info("{} {} - {}", LogUtils.CONFIG_PREFIX, "beanName", entry.getKey());
                logger.info("{}\t{} - {}", LogUtils.CONFIG_PREFIX, "mode", "standalone");
                logger.info("{}\t{} - {}", LogUtils.CONFIG_PREFIX, "jedisPoolConfig@type", poolConfig.getClass());
                logger.info("{}\t{} - {}", LogUtils.CONFIG_PREFIX, "address", jedisClient.getAddress());
                logger.info("{}\t{} - {}", LogUtils.CONFIG_PREFIX, "port", jedisClient.getPort());
                logJedisPoolConfigProps(poolConfig);
                logger.info(LogUtils.LINE);
                LogUtils.clearContext();
            }
        };
    }

    @Bean
    public ApplicationListener<ApplicationEvent> jedisClusterConfigListener() {
        return new AbstractConfigPrintSpringListener<JedisClusterClient>() {
            @Override
            protected Class<JedisClusterClient> getConfigClass() {
                return JedisClusterClient.class;
            }

            @Override
            protected String getModuleName() {
                return "Jedis Cluster";
            }

            @Override
            protected void logConfigInfo(Map.Entry<String, JedisClusterClient> entry) {
                if (entry == null)
                    return;
                JedisClusterClient clusterClient = entry.getValue();
                JedisPoolConfig poolConfig = clusterClient.getJedisPoolConfig();
                LogUtils.putContextColumn1("config");
                LogUtils.putContextColumn2("jedis-cluster:" +
                        StringUtils.substringBefore(entry.getKey(), JedisClusterClient.class.getSimpleName()) +
                        ":" + DateTime.now().toString("yyyyMMddHHmmss"));
                logger.info("{} {} - {}", LogUtils.CONFIG_PREFIX, "beanName", entry.getKey());
                logger.info("{}\t{} - {}", LogUtils.CONFIG_PREFIX, "mode", "cluster");
                logger.info("{}\t{} - {}", LogUtils.CONFIG_PREFIX, "jedisPoolConfig@type", poolConfig.getClass());
                logger.info("{}\t{} - {}", LogUtils.CONFIG_PREFIX, "address", clusterClient.getAddress());
                logger.info("{}\t{} - {}", LogUtils.CONFIG_PREFIX, "defaultConnectMaxAttempts", clusterClient.getDefaultConnectMaxAttempts());
                logger.info("{}\t{} - {}", LogUtils.CONFIG_PREFIX, "defaultConnectTimeout", clusterClient.getDefaultConnectTimeout());
                logJedisPoolConfigProps(poolConfig);
                logger.info(LogUtils.LINE);
                LogUtils.clearContext();
            }
        };
    }

    private void logJedisPoolConfigProps(JedisPoolConfig poolConfig) {
        logger.info("{}\t{} - {}", LogUtils.CONFIG_PREFIX, "maxIdle", poolConfig.getMaxIdle());
        logger.info("{}\t{} - {}", LogUtils.CONFIG_PREFIX, "maxTotal", poolConfig.getMaxTotal());
        logger.info("{}\t{} - {}", LogUtils.CONFIG_PREFIX, "minIdle", poolConfig.getMinIdle());
        logger.info("{}\t{} - {}", LogUtils.CONFIG_PREFIX, "maxWaitMillis", poolConfig.getMaxWaitMillis());
        logger.info("{}\t{} - {}", LogUtils.CONFIG_PREFIX, "minEvictableIdleTimeMillis", poolConfig.getMinEvictableIdleTimeMillis());
        logger.info("{}\t{} - {}", LogUtils.CONFIG_PREFIX, "numTestsPerEvictionRun", poolConfig.getNumTestsPerEvictionRun());
        logger.info("{}\t{} - {}", LogUtils.CONFIG_PREFIX, "softMinEvictableIdleTimeMillis", poolConfig.getSoftMinEvictableIdleTimeMillis());
        logger.info("{}\t{} - {}", LogUtils.CONFIG_PREFIX, "timeBetweenEvictionRunsMillis", poolConfig.getTimeBetweenEvictionRunsMillis());
        logger.info("{}\t{} - {}", LogUtils.CONFIG_PREFIX, "blockWhenExhausted", poolConfig.getBlockWhenExhausted());
        logger.info("{}\t{} - {}", LogUtils.CONFIG_PREFIX, "evictionPolicyClassName", poolConfig.getEvictionPolicyClassName());
        logger.info("{}\t{} - {}", LogUtils.CONFIG_PREFIX, "fairness", poolConfig.getFairness());
        logger.info("{}\t{} - {}", LogUtils.CONFIG_PREFIX, "jmxEnabled", poolConfig.getJmxEnabled());
        logger.info("{}\t{} - {}", LogUtils.CONFIG_PREFIX, "jmxNameBase", poolConfig.getJmxNameBase());
        logger.info("{}\t{} - {}", LogUtils.CONFIG_PREFIX, "jmxNamePrefix", poolConfig.getJmxNamePrefix());
        logger.info("{}\t{} - {}", LogUtils.CONFIG_PREFIX, "lifo", poolConfig.getLifo());
        logger.info("{}\t{} - {}", LogUtils.CONFIG_PREFIX, "testOnBorrow", poolConfig.getTestOnBorrow());
        logger.info("{}\t{} - {}", LogUtils.CONFIG_PREFIX, "testOnCreate", poolConfig.getTestOnCreate());
        logger.info("{}\t{} - {}", LogUtils.CONFIG_PREFIX, "testOnReturn", poolConfig.getTestOnReturn());
        logger.info("{}\t{} - {}", LogUtils.CONFIG_PREFIX, "testWhileIdle", poolConfig.getTestWhileIdle());
    }

}
