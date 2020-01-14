package com.mwb.maf.core.rocketmq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.mwb.maf.core.rocketmq.core.consumer.RocketMQConsumerConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.protocol.body.ConsumerRunningInfo;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.util.Assert;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RocketMQSimpleConsumer extends RocketMQBaseConsumer {

    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    private DefaultMQPushConsumer consumer;

//			caf.mq.rocketmq.consumer.subExpression = *
//			caf.mq.rocketmq.consumer.adjustThreadPoolNumsThreshold = 100000
//			caf.mq.rocketmq.consumer.clientCallbackExecutorThreads = 6
//			caf.mq.rocketmq.consumer.consumeConcurrentlyMaxSpan = 2000
//			caf.mq.rocketmq.consumer.consumeMessageBatchMaxSize = 1
//			caf.mq.rocketmq.consumer.consumeThreadMax = 64
//			caf.mq.rocketmq.consumer.consumeThreadMin = 20
//			caf.mq.rocketmq.consumer.consumeTimeout = 15
//			caf.mq.rocketmq.consumer.consumeTimestamp = 20181220185904
//			caf.mq.rocketmq.consumer.heartbeatBrokerInterval = 30000
//			caf.mq.rocketmq.consumer.maxReconsumeTimes = -1
//			caf.mq.rocketmq.consumer.persistConsumerOffsetInterval = 5000
//			caf.mq.rocketmq.consumer.pollNameServerInterval = 30000
//			caf.mq.rocketmq.consumer.postSubscriptionWhenPull = false
//			caf.mq.rocketmq.consumer.pullBatchSize = 32
//			caf.mq.rocketmq.consumer.pullInterval = 0
//			caf.mq.rocketmq.consumer.pullThresholdForQueue = 1000
//			caf.mq.rocketmq.consumer.pullThresholdForTopic = -1
//			caf.mq.rocketmq.consumer.pullThresholdSizeForQueue = 100
//			caf.mq.rocketmq.consumer.pullThresholdSizeForTopic = -1
//			caf.mq.rocketmq.consumer.suspendCurrentQueueTimeMillis = 1000
//			caf.mq.rocketmq.consumer.unitMode = false
//			caf.mq.rocketmq.consumer.vipChannelEnabled = true


    public RocketMQSimpleConsumer(RocketMQConsumerConfig consumerConfig) throws Exception {
        super(consumerConfig);
        // Instantiate with specified consumer group name.
        consumer = new DefaultMQPushConsumer();

        // Specify name server addresses.
        consumer.setNamesrvAddr(consumerConfig.getNamesrvAddr());

        // (1).consumer-config
        consumer.setAdjustThreadPoolNumsThreshold(consumerConfig.getAdjustThreadPoolNumsThreshold());
        consumer.setConsumeConcurrentlyMaxSpan(consumerConfig.getConsumeConcurrentlyMaxSpan());
        consumer.setConsumeMessageBatchMaxSize(consumerConfig.getConsumeMessageBatchMaxSize());
        consumer.setConsumeThreadMax(consumerConfig.getConsumeThreadMax());
        consumer.setConsumeThreadMin(consumerConfig.getConsumeThreadMin());
        consumer.setConsumeTimeout(consumerConfig.getConsumeTimeout());
        //consumer.setConsumeTimestamp(consumerConfig.getConsumeTimestamp());
        //consumer.setMaxReconsumeTimes(consumerConfig.getMaxReconsumeTimes());

        consumer.setPullBatchSize(consumerConfig.getPullBatchSize());
        consumer.setPullInterval(consumerConfig.getPullInterval());
        consumer.setPullThresholdForQueue(consumerConfig.getPullThresholdForQueue());
        consumer.setPullThresholdForTopic(consumerConfig.getPullThresholdForTopic());
        consumer.setPullThresholdSizeForQueue(consumerConfig.getPullThresholdSizeForQueue());
        consumer.setPullThresholdSizeForTopic(consumerConfig.getPullThresholdSizeForTopic());
        consumer.setSuspendCurrentQueueTimeMillis(consumerConfig.getSuspendCurrentQueueTimeMillis());

        // (2).client-config
        consumer.setHeartbeatBrokerInterval(consumerConfig.getHeartbeatBrokerInterval());
        //consumer.setUnitMode(consumerConfig.isUnitMode());
        consumer.setVipChannelEnabled(consumerConfig.isVipChannelEnabled());
        consumer.setPersistConsumerOffsetInterval(consumerConfig.getPersistConsumerOffsetInterval());
        consumer.setPollNameServerInterval(consumerConfig.getPollNameServerInterval());
        //consumer.setClientCallbackExecutorThreads(consumerConfig.getClientCallbackExecutorThreads());

        // consumer.setUnitName(consumerConfig.getUnitName());
        // consumer.setAllocateMessageQueueStrategy(allocateMessageQueueStrategy);
        // consumer.setClientIP(clientIP);
        // consumer.setConsumeFromWhere(consumeFromWhere);
        // consumer.setConsumerGroup(consumerGroup);
        // consumer.setInstanceName(consumerConfig.getInstanceName());
        // consumer.setPostSubscriptionWhenPull(consumerConfig.getPostSubscriptionWhenPull());
        // consumer.setUseTLS(consumerConfig.isUseTLS());
    }

    @Override
    public void subscribe(String topic, String subExpression) throws Exception {
        // Subscribe one more more topics to consume.
        consumer.subscribe(topic, subExpression);
    }

    @Override
    public void subscribe(String topic) throws Exception {
        // Subscribe one more more topics to consume.
        consumer.subscribe(topic, getRocketConsumerConfig().getSubExpression());
    }

    @Override
    public void setMessageModel(MessageModel msgModel) {
        consumer.setMessageModel(msgModel);
    }

    @Override
    public void start() throws Exception {
        consumer.setConsumerGroup(getRocketConsumerConfig().getConsumerGroup());

        Assert.isTrue(StringUtils.isNotEmpty(consumer.getConsumerGroup()), "consumerGroup must be specified!");
        Assert.isTrue(!RocketMQConsumerConfig.DEFAULT_CONSUMER_GROUP.equals(consumer.getConsumerGroup()),
                "consumerGroup must be specified! It is forbided to use the default group!");

        log.info("begin add shutdownHook for rocketmq consumer.");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                consumer.shutdown();
                log.info("rocketmq consumer has shutdown gracefully!");
            } catch (Exception e) {
                log.error("error occurred during rocketmq consumer shutdown! pls check! ", e);
            }
        }));
        log.info("rocketmq consumer shutdown hook added!");

        consumer.start();

        startConsumerStat();
    }

    private void startConsumerStat() {
//		ConsumeStatus consumerStatus = consumer.getDefaultMQPushConsumerImpl().getConsumerStatsManager()
//				.consumeStatus(consumer.getConsumerGroup(), topic);

        executorService.scheduleAtFixedRate(() -> {

            ConsumerRunningInfo consumerRunningInfo = consumer.getDefaultMQPushConsumerImpl().consumerRunningInfo();

            log.info(JSON.toJSONString(consumerRunningInfo, SerializerFeature.PrettyFormat,
                    SerializerFeature.WriteClassName));

        }, 60, 60, TimeUnit.SECONDS);
    }

    @Override
    public void registerMessageListener(MessageListenerConcurrently messageListener) {
        // Register callback to execute on arrival of messages fetched from brokers.
        consumer.registerMessageListener(messageListener);
    }

    @Override
    public void logConfig() {
        log.info(JSON.toJSONString(this.consumer, SerializerFeature.PrettyFormat, SerializerFeature.WriteClassName));
    }

}
