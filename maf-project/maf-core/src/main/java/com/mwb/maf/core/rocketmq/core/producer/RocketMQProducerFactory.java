package com.mwb.maf.core.rocketmq.core.producer;

import com.mwb.maf.core.rocketmq.producer.RocketMQBaseProducer;
import com.mwb.maf.core.rocketmq.producer.RocketMQRestrySendMessageScheduler;
import com.mwb.maf.core.rocketmq.producer.RocketMQSimpleProducer;
import com.mwb.maf.core.util.CustomizedConfigurationPropertiesBinder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

@Slf4j
public class RocketMQProducerFactory extends RocketMQBaseProducerFactory
        implements FactoryBean<RocketMQBaseProducer>, EnvironmentAware, BeanNameAware {
    private Environment environment;
    private String beanName;

    @Autowired
    protected CustomizedConfigurationPropertiesBinder binder;

    @Override
    public RocketMQBaseProducer getObject() throws Exception {

        RocketMQProducerConfig producerConfig = new RocketMQProducerConfig();
        Bindable<?> target = Bindable.of(RocketMQProducerConfig.class).withExistingValue(producerConfig);

        binder.bind(getRocketPrefix(), target);
        binder.bind(getRocketProducerPrefix(), target);

        String producerGroup = RocketMQProducerRegistrar.producerGroupMap.get(beanName);
        if (StringUtils.isBlank(producerGroup)) {
            producerConfig.setProducerGroup(null);
        } else {
            producerConfig.setProducerGroup(RocketMQProducerRegistrar.producerGroupMap.get(beanName));
        }
        log.info("init producerConfig:" + producerConfig.getProducerGroup());

        RocketMQBaseProducer mqProducer = new RocketMQSimpleProducer(producerConfig, beanName);
        mqProducer.logConfig();

        // 获取mq的retry params.
        RocketMQProducerCafConfig commonConfig = new RocketMQProducerCafConfig();
        target = Bindable.of(RocketMQProducerCafConfig.class).withExistingValue(commonConfig);
        binder.bind(getRocketProducerPrefix(), target);
        commonConfig.logConfig();

        // 初始化消息重发机制
        RocketMQRestrySendMessageScheduler.start(commonConfig);
        // RocketMQProducerExceptionStat.start();

        // TODO
//		executorService.scheduleAtFixedRate(() -> {
//			JedisCluster jedisCluster = jedisClusterClient.getDriver();
//			Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
//			Logger logger = Loggers.getPerformanceLogger();
//			LogUtils.putContextColumn1("health");
//			LogUtils.putContextColumn2(
//					"jedisCluster" + ":" + namespace + ":" + DateTime.now().toString("yyyyMMddHHmmss"));
//			logger.info("{} : {}", "address", address);
//			if (MapUtils.isNotEmpty(clusterNodes)) {
//				for (Map.Entry<String, JedisPool> poolEntry : clusterNodes.entrySet()) {
//					String node = poolEntry.getKey();
//					JedisPool jedisPool = poolEntry.getValue();
//					logger.info("node : {}\t{} : {}", node, "numActive", jedisPool.getNumActive());
//					logger.info("node : {}\t{} : {}", node, "numIdle", jedisPool.getNumIdle());
//					logger.info("node : {}\t{} : {}", node, "numWaiters", jedisPool.getNumWaiters());
//					logger.info("node : {}\t{} : {}", node, "MaxBorrowWaitTimeMillis",
//							jedisPool.getMaxBorrowWaitTimeMillis());
//					logger.info("node : {}\t{} : {}", node, "meanBorrowWaitTimeMillis",
//							jedisPool.getMeanBorrowWaitTimeMillis());
//				}
//			}
//			logger.info(LogUtils.LINE);
//			LogUtils.clearContext();
//		}, 30, 30, TimeUnit.SECONDS);

        if (RocketMQProducerRegistrar.autoStartSwitchMap.get(beanName)) {
            mqProducer.start();
        }
        return mqProducer;
    }

    protected String getPreFix() {
        return PREFIX_APP_ROCKET_PRODUCER;
    }

    @Override
    public Class<?> getObjectType() {
        return RocketMQBaseProducer.class;
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
