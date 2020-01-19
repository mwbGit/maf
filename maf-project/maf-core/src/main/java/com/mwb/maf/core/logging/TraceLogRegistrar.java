package com.mwb.maf.core.logging;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.mwb.maf.core.db.DataSourceValueBindingBeanPostProcessor;
import com.mwb.maf.core.util.BeanRegistrationUtil;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;


public class TraceLogRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    private ResourceLoader resourceLoader;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        boolean processed = false;
        {
            AnnotationAttributes attributes = AnnotationAttributes.fromMap(importingClassMetadata
                    .getAnnotationAttributes(EnableTraceLog.class.getName()));
            if (attributes != null) {
                dealOne(registry, attributes);
                processed = true;
            }
        }
        if (!processed)
            throw new IllegalStateException("no @EnableDataSource or @EnableDataSources found! pls check!");
    }

    private void dealOne(BeanDefinitionRegistry registry, AnnotationAttributes oneAttributes) {
        Boolean switchWrite = oneAttributes.getBoolean("write");
        if (BooleanUtils.isFalse(switchWrite)){
            return;
        }
        String namespace = oneAttributes.getString("namespace");
        Assert.isTrue(StringUtils.isNotEmpty(namespace), "namespace must be specified!");

        scanAndRegisterMappers(registry, namespace, new String[]{"com.mwb.maf.core.logging.mapper"});

        BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(
                registry, namespace + DataSourceValueBindingBeanPostProcessor.class.getSimpleName(), DataSourceValueBindingBeanPostProcessor.class
        );
        BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(
                registry, namespace + DataSource.class.getSimpleName(), DruidDataSource.class
        );
        BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(
                registry, namespace + SqlSessionFactory.class.getSimpleName(), SqlSessionFactoryBean.class
        );
        BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(
                registry, namespace + DataSourceTransactionManager.class.getSimpleName(), DataSourceTransactionManager.class
        );
        BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(
                registry, namespace + StatFilter.class.getSimpleName(), StatFilter.class
        );

        BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(
                registry, namespace + LoggingDbNotice.class.getSimpleName(), LoggingDbNotice.class
        );
    }

    private void scanAndRegisterMappers(BeanDefinitionRegistry registry, String namespace, String[] mapperPackages) {
        ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);

        if (resourceLoader != null) {
            scanner.setResourceLoader(resourceLoader);
        }

        Class<? extends Annotation> annotationClass = Annotation.class;
        if (!Annotation.class.equals(annotationClass)) {
            scanner.setAnnotationClass(annotationClass);
        }

        Class<?> markerInterface = Class.class;
        if (!Class.class.equals(markerInterface)) {
            scanner.setMarkerInterface(markerInterface);
        }

        Class<? extends BeanNameGenerator> generatorClass = BeanNameGenerator.class;
        if (!BeanNameGenerator.class.equals(generatorClass)) {
            scanner.setBeanNameGenerator(BeanUtils.instantiateClass(generatorClass));
        }

        Class<? extends MapperFactoryBean> mapperFactoryBeanClass = MapperFactoryBean.class;
        if (!MapperFactoryBean.class.equals(mapperFactoryBeanClass)) {
            scanner.setMapperFactoryBean(BeanUtils.instantiateClass(mapperFactoryBeanClass));
        }

        scanner.setSqlSessionTemplateBeanName("");
        scanner.setSqlSessionFactoryBeanName(namespace + SqlSessionFactory.class.getSimpleName());

        List<String> basePackages = new ArrayList<String>();
        for (String pkg : mapperPackages) {
            if (StringUtils.isNotEmpty(pkg)) {
                basePackages.add(pkg);
            }
        }
        scanner.registerFilters();

        scanner.doScan(basePackages.toArray(new String[0]));
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

}