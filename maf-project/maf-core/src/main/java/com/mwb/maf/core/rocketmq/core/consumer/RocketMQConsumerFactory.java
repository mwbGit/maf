package com.mwb.maf.core.rocketmq.core.consumer;

import com.mwb.maf.core.rocketmq.consumer.RocketMQBaseConsumer;
import com.mwb.maf.core.rocketmq.consumer.RocketMQSimpleConsumer;
import com.mwb.maf.core.util.CustomizedConfigurationPropertiesBinder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

@Slf4j
public class RocketMQConsumerFactory extends RocketMQBaseConsumerFactory
        implements FactoryBean<RocketMQBaseConsumer>, EnvironmentAware, BeanNameAware {
    private Environment environment;
    private String beanName;

    @Autowired
    protected CustomizedConfigurationPropertiesBinder binder;

    @Override
    public RocketMQBaseConsumer getObject() throws Exception {

        RocketMQConsumerConfig consumerConfig = new RocketMQConsumerConfig();
        Bindable<?> target = Bindable.of(RocketMQConsumerConfig.class).withExistingValue(consumerConfig);
        binder.bind(getRocketPrefix(), target);
        binder.bind(getRocketConsumerPrefix(), target);

        consumerConfig.setConsumerGroup(null);
        RocketMQSimpleConsumer mqConsumer = new RocketMQSimpleConsumer(consumerConfig);
        mqConsumer.logConfig();

        return mqConsumer;
    }

    @Override
    public Class<?> getObjectType() {
        return RocketMQConsumerFactory.class;
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
