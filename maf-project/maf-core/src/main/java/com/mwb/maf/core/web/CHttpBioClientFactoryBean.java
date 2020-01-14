package com.mwb.maf.core.web;

import com.mwb.maf.core.util.CustomizedConfigurationPropertiesBinder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

public class CHttpBioClientFactoryBean
        implements FactoryBean<CHttpBioClient>, EnvironmentAware, BeanNameAware {
    public static final String PREFIX_APP_JEDIS = "app.chttpbioclient";
    private Environment environment;
    private String beanName;

    @Autowired
    protected CustomizedConfigurationPropertiesBinder binder;

    @Override
    public CHttpBioClient getObject() {
        String namespace = StringUtils.substringBefore(beanName, CHttpBioClient.class.getSimpleName());

        CHttpBioClientProps props = new CHttpBioClientProps();
        Bindable<?> target = Bindable.of(CHttpBioClientProps.class).withExistingValue(props);
        binder.bind(getPreFix() + "." + namespace + ".props", target);

        CHttpBioClient cHttpBioClient = new CHttpBioClient(namespace, props);
        cHttpBioClient.init();

        return cHttpBioClient;
    }

    private String getPreFix() {
        return PREFIX_APP_JEDIS;
    }

    @Override
    public Class<?> getObjectType() {
        return CHttpBioClient.class;
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
