package com.mwb.maf.core.sentinel;

import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import com.mwb.maf.core.base.CustomizedPropertiesBinderAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

@Configuration
@ConditionalOnClass({
        SphU.class
})
@AutoConfigureAfter(CustomizedPropertiesBinderAutoConfiguration.class)
public class SentinelAutoConfiguration {
    @Bean
    public SentinelMetricsAdapter sentinelMetricsAdapter() {
        SentinelMetricsAdapter adapter = new SentinelMetricsAdapter();
        adapter.init();
        return adapter;
    }

    @Bean
    public SentinelResourceAspect sentinelResourceAspect() {
        return new SentinelResourceAspect();
    }

    @Bean
    public SentinelHttpRestController sentinelHttpRestController() {
        return new SentinelHttpRestController();
    }

    @Bean
    public FilterRegistrationBean sentinelFilterRegistration() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new SentinelHttpFilter());
        registration.addUrlPatterns("/*");
        registration.setName("sentinelHttpFilter");
        registration.setOrder(1);

        return registration;
    }
}
