package com.mwb.maf.core.kv;

import com.mwb.maf.core.logging.Loggers;
import com.mwb.maf.core.util.CustomizedConfigurationPropertiesBinder;
import com.mwb.maf.core.util.LogUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class JedisClusterClientFactoryBean extends BaseJedisConfiguration
        implements FactoryBean<JedisClusterClient>, EnvironmentAware, BeanNameAware {
    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private Environment environment;
    private String beanName;

    @Autowired
    protected CustomizedConfigurationPropertiesBinder binder;

    @Override
    public JedisClusterClient getObject() throws NoSuchFieldException {
        String namespace = StringUtils.substringBefore(beanName, JedisClusterClient.class.getSimpleName());

        String addressKey = getPreFix() + "." + namespace + ".address";
        String address = environment.getProperty(addressKey);
        Assert.isTrue(StringUtils.isNotEmpty(address), String.format("%s=%s must be not null! ", addressKey, address));

        JedisPoolConfig jedisPoolConfig = createJedisPoolConfig();
        jedisPoolConfig.setTestOnReturn(false);
        Bindable<?> target = Bindable.of(JedisPoolConfig.class).withExistingValue(jedisPoolConfig);
        binder.bind(getPreFix() + "." + namespace + ".pool", target);

        JedisClusterClient jedisClusterClient = new JedisClusterClient(namespace, jedisPoolConfig, address);

        executorService.scheduleAtFixedRate(() -> {
            JedisCluster jedisCluster = jedisClusterClient.getDriver();
            Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
            Logger logger = Loggers.getPerformanceLogger();
            LogUtils.putContextColumn1("health");
            LogUtils.putContextColumn2("jedisCluster" + ":" + namespace + ":" + DateTime.now().toString("yyyyMMddHHmmss"));
            logger.info("{} : {}", "address", address);
            if (MapUtils.isNotEmpty(clusterNodes)) {
                for (Map.Entry<String, JedisPool> poolEntry : clusterNodes.entrySet()) {
                    String node = poolEntry.getKey();
                    JedisPool jedisPool = poolEntry.getValue();
                    logger.info("node : {}\t{} : {}", node, "numActive", jedisPool.getNumActive());
                    logger.info("node : {}\t{} : {}", node, "numIdle", jedisPool.getNumIdle());
                    logger.info("node : {}\t{} : {}", node, "numWaiters", jedisPool.getNumWaiters());
                    logger.info("node : {}\t{} : {}", node, "MaxBorrowWaitTimeMillis", jedisPool.getMaxBorrowWaitTimeMillis());
                    logger.info("node : {}\t{} : {}", node, "meanBorrowWaitTimeMillis", jedisPool.getMeanBorrowWaitTimeMillis());
                }
            }
            logger.info(LogUtils.LINE);
            LogUtils.clearContext();
        }, 30, 30, TimeUnit.SECONDS);

        return jedisClusterClient;
    }

    protected String getPreFix() {
        return PREFIX_APP_JEDIS_CLUSTER;
    }

    @Override
    public Class<?> getObjectType() {
        return JedisClusterClient.class;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }
}
