package com.mwb.maf.core.rocketmq.core.producer;

import com.mwb.maf.core.util.BeanRegistrationUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RocketMQProducerRegistrar implements ImportBeanDefinitionRegistrar {

    static Map<String, String> producerGroupMap = new ConcurrentHashMap<String, String>();

    static Map<String, Boolean> autoStartSwitchMap = new ConcurrentHashMap<String, Boolean>();

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        boolean processed = false;
        {
            AnnotationAttributes attributes = AnnotationAttributes
                    .fromMap(importingClassMetadata.getAnnotationAttributes(EnableRocketMQProducer.class.getName()));
            if (attributes != null) {
                dealOne(registry, attributes);
                processed = true;
            }
        }
        {
            AnnotationAttributes attributes = AnnotationAttributes
                    .fromMap(importingClassMetadata.getAnnotationAttributes(EnableRocketMQProducers.class.getName()));
            if (attributes != null) {
                AnnotationAttributes[] annotationArray = attributes.getAnnotationArray("value");
                if (annotationArray != null && annotationArray.length > 0) {
                    for (AnnotationAttributes oneAttributes : annotationArray) {
                        dealOne(registry, oneAttributes);
                        processed = true;
                    }
                }
            }
        }
        if (!processed)
            throw new IllegalStateException("no @EnableRocketProducer or @EnableRocketProducers found! pls check!");
    }

    private void dealOne(BeanDefinitionRegistry registry, AnnotationAttributes oneAttributes) {
        String namespace = oneAttributes.getString("namespace");
        String producerGroup = oneAttributes.getString("producerGroup");
        boolean autoStart = oneAttributes.getBoolean("autoStart");

        producerGroupMap.put(namespace, producerGroup);
        autoStartSwitchMap.put(namespace, autoStart);

        Assert.isTrue(StringUtils.isNotEmpty(namespace), "namespace must be specified!");

        BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(registry, namespace,
                RocketMQProducerFactory.class);
    }

}
