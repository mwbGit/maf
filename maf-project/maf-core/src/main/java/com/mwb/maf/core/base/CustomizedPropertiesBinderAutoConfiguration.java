package com.mwb.maf.core.base;

import com.mwb.maf.core.util.CustomizedConfigurationPropertiesBinder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

@Order(1)
public class CustomizedPropertiesBinderAutoConfiguration {
    @Bean
    public CustomizedConfigurationPropertiesBinder customizedConfigurationPropertiesBinder() {
        return new CustomizedConfigurationPropertiesBinder();
    }
}
