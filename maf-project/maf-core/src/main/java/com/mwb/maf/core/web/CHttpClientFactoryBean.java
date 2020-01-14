package com.mwb.maf.core.web;

import com.mwb.maf.core.util.CustomizedConfigurationPropertiesBinder;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.nio.reactor.IOReactorException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class CHttpClientFactoryBean
        implements FactoryBean<CHttpClient>, EnvironmentAware, BeanNameAware {
    public static final String PREFIX_APP_JEDIS = "app.chttpclient";
    private Environment environment;
    private String beanName;
    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    @Autowired
    protected CustomizedConfigurationPropertiesBinder binder;

    @Override
    public CHttpClient getObject() throws IOReactorException {
        String namespace = StringUtils.substringBefore(beanName, CHttpClient.class.getSimpleName());

        CHttpClientProps props = new CHttpClientProps();
        Bindable<?> target = Bindable.of(CHttpClientProps.class).withExistingValue(props);
        binder.bind(getPreFix() + "." + namespace + ".props", target);

        CHttpClient cHttpClient = new CHttpClient(namespace, props);
        cHttpClient.init();


        return cHttpClient;
    }

    private String getPreFix() {
        return PREFIX_APP_JEDIS;
    }

    @Override
    public Class<?> getObjectType() {
        return CHttpClient.class;
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
