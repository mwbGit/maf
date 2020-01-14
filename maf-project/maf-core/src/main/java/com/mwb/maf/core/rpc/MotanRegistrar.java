package com.mwb.maf.core.rpc;

import com.mwb.maf.core.util.BeanRegistrationUtil;
import com.weibo.api.motan.config.springsupport.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

public class MotanRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        boolean processed = false;
        {
            AnnotationAttributes attributes = AnnotationAttributes.fromMap(importingClassMetadata
                    .getAnnotationAttributes(EnableMotan.class.getName()));
            if (attributes != null) {
                dealOne(registry, attributes);
                processed = true;
            }
        }
        {
            AnnotationAttributes attributes = AnnotationAttributes.fromMap(importingClassMetadata
                    .getAnnotationAttributes(EnableMotans.class.getName()));
            if (attributes != null) {
                AnnotationAttributes[] annotationArray = attributes.getAnnotationArray("value");
                for (AnnotationAttributes oneAttributes : annotationArray) {
                    dealOne(registry, oneAttributes);
                    processed = true;
                }
            }
        }
        if (!processed)
            throw new IllegalStateException("no @EnableMotan or @EnableMotans found! pls check!");
    }

    private void dealOne(BeanDefinitionRegistry registry, AnnotationAttributes oneAttributes) {
        String namespace = oneAttributes.getString("namespace");
        Assert.isTrue(StringUtils.isNotEmpty(namespace), "namespace must be specified!");
        BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(
                registry, namespace + MotanValueBindingBeanPostProcessor.class.getSimpleName(), MotanValueBindingBeanPostProcessor.class
        );
        BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(
                registry, AnnotationBean.class.getSimpleName(), AnnotationBean.class
        );
        BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(
                registry, namespace + RegistryConfigBean.class.getSimpleName(), RegistryConfigBean.class
        );
        BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(
                registry, namespace + ProtocolConfigBean.class.getSimpleName(), ProtocolConfigBean.class
        );
        BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(
                registry, namespace + BasicServiceConfigBean.class.getSimpleName(), BasicServiceConfigBean.class
        );
        BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(
                registry, namespace + BasicRefererConfigBean.class.getSimpleName(), BasicRefererConfigBean.class
        );
    }

}
