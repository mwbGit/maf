package com.mwb.maf.core.web;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
@ConditionalOnClass(CHttpClient.class)
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE)
@ConditionalOnWebApplication
public class CHttpClientAutoConfiguration {
    @Bean
    public HealthIndicator chttpclientHealthIndicator() {
        return () -> {
            if (CHttpClient.readyForRequest()) {
                return Health.up().build();
            } else {
                return Health.down().withDetail("CHttpClient", "Down").build();
            }
        };
    }

    @Bean
    public HealthIndicator chttpbioclientHealthIndicator() {
        return () -> {
            if (CHttpBioClient.readyForRequest()) {
                return Health.up().build();
            } else {
                return Health.down().withDetail("CHttpBioClient", "Down").build();
            }
        };
    }
}
