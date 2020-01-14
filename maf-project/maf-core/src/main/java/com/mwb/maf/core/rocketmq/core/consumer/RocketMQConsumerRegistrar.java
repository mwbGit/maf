package com.mwb.maf.core.rocketmq.core.consumer;

import com.mwb.maf.core.util.BeanRegistrationUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

public class RocketMQConsumerRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        boolean processed = false;
        {
            AnnotationAttributes attributes = AnnotationAttributes
                    .fromMap(importingClassMetadata.getAnnotationAttributes(EnableRocketMQConsumer.class.getName()));
            if (attributes != null) {
                dealOne(registry, attributes);
                processed = true;
            }
        }
        {
            AnnotationAttributes attributes = AnnotationAttributes
                    .fromMap(importingClassMetadata.getAnnotationAttributes(EnableRocketMQConsumers.class.getName()));
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
            throw new IllegalStateException("no @EnableRocketConsumer or @EnableRocketConsumers found! pls check!");
    }

    private void dealOne(BeanDefinitionRegistry registry, AnnotationAttributes oneAttributes) {
        String namespace = oneAttributes.getString("namespace");
        Assert.isTrue(StringUtils.isNotEmpty(namespace), "namespace must be specified!");

        BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(registry, namespace,
                RocketMQConsumerFactory.class);

    }

}
