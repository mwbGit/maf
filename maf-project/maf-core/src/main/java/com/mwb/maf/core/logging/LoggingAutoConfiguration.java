package com.mwb.maf.core.logging;

import org.springframework.context.annotation.Bean;

public class LoggingAutoConfiguration {

    @Bean
    public LoggingNoticeListener loggingNoticeListener() {
        return new LoggingNoticeListener();
    }

    @Bean
    public LoggingFactoryBean loggingFactoryBean() {
        return new LoggingFactoryBean();
    }

}
