package com.mwb.maf.core.metrics;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

@Setter
@Getter
@ToString
public class MonitorConfig {
    @Value("${app.monitor.prometheus.port:9145}")
    private int port = 9145;
    @Value("${app.monitor.log.delay:30}")
    private int logDelay = 30;
    @Value("${app.monitor.log.period:60}")
    private int logPeriod = 60;

    @Value("${app.monitor.profile.jedis.enable:true}")
    private boolean enableJedisProfile = true;

    @Value("${app.monitor.profile.motan.enable:true}")
    private boolean enableMotanProfile = true;

    @Value("${app.motan.sentinel.enable:false}")
    private boolean enableMotanSentinel = false;

    @Value("${app.monitor.profile.http.enable:true}")
    private boolean enableHttpProfile = true;

    @Value("${app.monitor.profile.mapper.enable:true}")
    private boolean enableMapperProfile = true;

    @Value("${app.monitor.custom.mapper.enable:true}")
    private boolean enableCustomProfile = true;

    @Value("${app.monitor.profile.rocketmq.producer.enable:true}")
    private boolean enableRocketMQProducerProfile = true;

    //@Value("${app.monitor.profile.rocketmq.consumer.enable:true}")
    //private boolean enableRocketMQProducerProfile = true;

    public static volatile boolean DYNAMIC_ENABLE_MAPPER_PROFILE = true;
    public static volatile boolean DYNAMIC_ENABLE_MOTAN_PROFILE = true;
    public static volatile boolean DYNAMIC_ENABLE_MOTAN_SENTINEL = true;

    @Scheduled(fixedRate = 60)
    public void refreshStaticProps() {
        DYNAMIC_ENABLE_MAPPER_PROFILE = enableMapperProfile;
        DYNAMIC_ENABLE_MOTAN_PROFILE = enableMotanProfile;
        DYNAMIC_ENABLE_MOTAN_SENTINEL = enableMotanSentinel;
    }
}
