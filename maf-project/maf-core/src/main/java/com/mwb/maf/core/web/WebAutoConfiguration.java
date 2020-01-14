package com.mwb.maf.core.web;

import com.mwb.maf.core.metrics.ProfilerHttpFilter;
import com.mwb.maf.core.sentinel.SentinelHttpInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;

@Configuration
@ConditionalOnClass(HttpServletRequest.class)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnWebApplication
public class WebAutoConfiguration {
    @Value("${app.monitor.profile.http.enable:true}")
    private boolean enableHttpProfiler;
    @Value("${app.trace.http.enable:true}")
    private boolean enableHttpTracingInterceptor;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public WebMvcConfigurer httpTracingConfig() {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                if (enableHttpProfiler) {
//                    registry.addInterceptor(new HttpProfilerInterceptor()).addPathPatterns("/**");
                }
                registry.addInterceptor(new SentinelHttpInterceptor()).addPathPatterns("/**");
                if (enableHttpTracingInterceptor) {
                    registry.addInterceptor(new HttpTracingInterceptor()).addPathPatterns("/**");
                }
            }
        };
    }

    @Bean
    public FilterRegistrationBean profilerFilterRegistration() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new ProfilerHttpFilter());
        registration.addUrlPatterns("/*");
        registration.setName("profilerHttpFilter");
        registration.setOrder(1);

        return registration;
    }

    //@Bean
    public ErrorController errorController() {
        return new CustomErrorController();
    }

    @Bean
    public ProjectInfoController projectInfoController() {
        return new ProjectInfoController();
    }
}
