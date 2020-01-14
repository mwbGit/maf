package com.mwb.maf.core.kv;

import com.mwb.maf.core.util.CustomizedConfigurationPropertiesBinder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import redis.clients.jedis.JedisPoolConfig;

public abstract class AbstractJedisConfiguration extends BaseJedisConfiguration implements EnvironmentAware {
    protected Environment environment;
    @Autowired
    protected CustomizedConfigurationPropertiesBinder binder;

    protected abstract String namespace();

    protected abstract JedisClient jedisClient();

    protected String getPreFix() {
        return PREFIX_APP_JEDIS;
    }

    protected JedisClient createJedisClient() {
        String key = getPreFix() + "." + namespace() + ".address";
        String address = environment.getProperty(key);
        Assert.isTrue(StringUtils.isNotEmpty(address), String.format("%s=%s must be not null! ", key, address));

        key = getPreFix() + "." + namespace() + ".port";
        String port = environment.getProperty(key);
        Assert.isTrue(StringUtils.isNotEmpty(port) && NumberUtils.isCreatable(port), String.format("%s=%s must be not null! and must be a number!", key, port));

        JedisPoolConfig jedisPoolConfig = createJedisPoolConfig();
        Bindable<?> target = Bindable.of(JedisPoolConfig.class).withExistingValue(jedisPoolConfig);
        binder.bind(getPreFix() + namespace() + ".pool", target);
        return new JedisClient(namespace(), jedisPoolConfig, address, Integer.parseInt(port));
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
