package com.mwb.maf.core.kv;

import com.mwb.maf.core.util.CustomizedConfigurationPropertiesBinder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import redis.clients.jedis.JedisPoolConfig;

public abstract class AbstractJedisClusterConfiguration extends BaseJedisConfiguration implements EnvironmentAware {

    protected Environment environment;
    @Autowired
    protected CustomizedConfigurationPropertiesBinder binder;


    protected JedisClusterClient createJedisClusterClient() throws NoSuchFieldException {
        String key = getPreFix() + "." + namespace() + ".address";
        String address = environment.getProperty(key);
        Assert.isTrue(StringUtils.isNotEmpty(address), key + " must be not null!");
        JedisPoolConfig jedisPoolConfig = createJedisPoolConfig();
        Bindable<?> target = Bindable.of(JedisPoolConfig.class).withExistingValue(jedisPoolConfig);
        binder.bind(getPreFix() + namespace() + ".pool", target);
        return new JedisClusterClient(namespace(), jedisPoolConfig, address);
    }

    protected abstract String namespace();

    public abstract JedisClusterClient jedisClusterClient() throws NoSuchFieldException;

    @Override
    protected JedisPoolConfig createJedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = super.createJedisPoolConfig();
        jedisPoolConfig.setTestOnReturn(false);
        return jedisPoolConfig;
    }

    protected String getPreFix() {
        return PREFIX_APP_JEDIS_CLUSTER;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
