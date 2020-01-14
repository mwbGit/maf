package com.mwb.maf.core.base;

import com.mwb.maf.core.metrics.CustomProfilerAspect;
import com.mwb.maf.core.metrics.MonitorConfig;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;

public class FrameworkConfigAutoConfiguration {

    @Bean
    public MonitorConfig monitorConfig() {
        return new MonitorConfig();
    }

    @Bean
    public CustomProfilerAspect customProfilerAspect() {
        return new CustomProfilerAspect();
    }

    @Bean
    public WarmUpRegistry warmUpRegistry() {
        return new WarmUpRegistry();
    }

    @Bean
    public HealthIndicator warmUpIndicator() {
        return () -> {
            if (WarmUpResult.isDone()) {
                return Health.up().build();
            } else {
                return Health.down().withDetail("warmUp", "NO").build();
            }
        };
    }
}
