package com.mwb.maf.core.rpc;

import com.mwb.maf.core.base.CustomizedPropertiesBinderAutoConfiguration;
import com.weibo.api.motan.config.springsupport.*;
import com.weibo.api.motan.registry.zookeeper.ZookeeperRegistryFactory;
import com.weibo.api.motan.transport.netty.NettyChannelFactory;
import com.weibo.api.motan.util.MotanSwitcherUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({
        MotanSwitcherUtil.class,
        ZookeeperRegistryFactory.class,
        ProtocolConfigBean.class,
        NettyChannelFactory.class
})
@ConditionalOnProperty(prefix = "app.motan.default", name = "enabled", havingValue = "true")
@AutoConfigureAfter(CustomizedPropertiesBinderAutoConfiguration.class)
public class MotanAutoConfiguration extends AbstractMotanConfiguration {

    @Override
    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties(prefix = "app.motan.default.annotation")
    protected AnnotationBean annotationBean() {
        AnnotationBean annotationBean = createAnnotationBean();
        return annotationBean;
    }

    @Override
    @Bean(name = "defaultProtocol")
    @ConditionalOnMissingBean
    @ConfigurationProperties(prefix = "app.motan.default.protocol")
    protected ProtocolConfigBean protocolConfigBean() {
        ProtocolConfigBean protocolConfig = createProtocolConfig();
        return protocolConfig;
    }

    @Override
    @Bean(name = "defaultRegistry")
    @ConditionalOnMissingBean
    @ConfigurationProperties(prefix = "app.motan.default.registry")
    protected RegistryConfigBean registryConfigBean() {
        RegistryConfigBean registryConfig = createRegistryConfig();
        return registryConfig;
    }

    @Override
    @Bean(name = "defaultBaseService")
    @ConditionalOnMissingBean
    @ConfigurationProperties(prefix = "app.motan.default.basic-service")
    protected BasicServiceConfigBean basicServiceConfigBean(
            @Qualifier("defaultRegistry") RegistryConfigBean defaultRegistry,
            @Qualifier("defaultProtocol") ProtocolConfigBean defaultProtocol) {
        BasicServiceConfigBean basicServiceConfig = createBasicServiceConfig(defaultRegistry, defaultProtocol, 10010);
        return basicServiceConfig;
    }

    @Override
    @Bean(name = "defaultBaseReferer")
    @ConditionalOnMissingBean
    @ConfigurationProperties(prefix = "app.motan.default.basic-referer")
    protected BasicRefererConfigBean basicRefererConfigBean(
            @Qualifier("defaultRegistry") RegistryConfigBean defaultRegistry,
            @Qualifier("defaultProtocol") ProtocolConfigBean defaultProtocol) {
        BasicRefererConfigBean basicRefererConfig = createBasicRefererConfig(defaultRegistry, defaultProtocol);
        return basicRefererConfig;
    }

}
