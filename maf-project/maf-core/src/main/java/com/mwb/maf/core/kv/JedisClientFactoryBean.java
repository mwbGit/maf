package com.mwb.maf.core.kv;

import com.mwb.maf.core.logging.Loggers;
import com.mwb.maf.core.util.CustomizedConfigurationPropertiesBinder;
import com.mwb.maf.core.util.LogUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class JedisClientFactoryBean extends BaseJedisConfiguration
        implements FactoryBean<JedisClient>, EnvironmentAware, BeanNameAware {
    private Environment environment;
    private String beanName;
    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    @Autowired
    protected CustomizedConfigurationPropertiesBinder binder;

    @Override
    public JedisClient getObject() {
        String namespace = StringUtils.substringBefore(beanName, JedisClient.class.getSimpleName());

        String addressKey = getPreFix() + "." + namespace + ".address";
        String address = environment.getProperty(addressKey);
        Assert.isTrue(StringUtils.isNotEmpty(address), String.format("%s=%s must be not null! ", addressKey, address));

        String portKey = getPreFix() + "." + namespace + ".port";
        String port = environment.getProperty(portKey);
        Assert.isTrue(StringUtils.isNotEmpty(port) && NumberUtils.isCreatable(port), String.format("%s=%s must be not null! and must be a number!", portKey, port));

        JedisPoolConfig jedisPoolConfig = createJedisPoolConfig();
        Bindable<?> target = Bindable.of(JedisPoolConfig.class).withExistingValue(jedisPoolConfig);
        binder.bind(getPreFix() + "." + namespace + ".pool", target);

        JedisClient jedisClient = new JedisClient(namespace, jedisPoolConfig, address, Integer.parseInt(port));
        JedisPool jedisPool = jedisClient.getJedisPool();
        executorService.scheduleAtFixedRate(() -> {
            Logger logger = Loggers.getPerformanceLogger();
            LogUtils.putContextColumn1("health");
            LogUtils.putContextColumn2("jedis" + ":" + namespace + ":" + DateTime.now().toString("yyyyMMddHHmmss"));
            logger.info("{} : {}", "address", address);
            logger.info("{} : {}", "port", port);
            logger.info("{} : {}", "numActive", jedisPool.getNumActive());
            logger.info("{} : {}", "numIdle", jedisPool.getNumIdle());
            logger.info("{} : {}", "numWaiters", jedisPool.getNumWaiters());
            logger.info("{} : {}", "MaxBorrowWaitTimeMillis", jedisPool.getMaxBorrowWaitTimeMillis());
            logger.info("{} : {}", "meanBorrowWaitTimeMillis", jedisPool.getMeanBorrowWaitTimeMillis());
            logger.info(LogUtils.LINE);
            LogUtils.clearContext();
        }, 30, 30, TimeUnit.SECONDS);

        return jedisClient;
    }

    protected String getPreFix() {
        return PREFIX_APP_JEDIS;
    }

    @Override
    public Class<?> getObjectType() {
        return JedisClient.class;
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
