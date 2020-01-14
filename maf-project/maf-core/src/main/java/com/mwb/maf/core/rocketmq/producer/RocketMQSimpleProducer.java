package com.mwb.maf.core.rocketmq.producer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.mwb.maf.core.rocketmq.core.producer.RocketMQProducerConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.util.Assert;

/**
 * @author hepengyuan
 * @version create time: 2018年10月25日 上午11:04:18
 */
@Slf4j
public class RocketMQSimpleProducer extends RocketMQBaseProducer {

    private DefaultMQProducer mqProducer;

    private String namespace;

//			caf.mq.rocketmq.producer.clientCallbackExecutorThreads = 6
//			caf.mq.rocketmq.producer.compressMsgBodyOverHowmuch = 4096
//			caf.mq.rocketmq.producer.defaultTopicQueueNums = 4
//			caf.mq.rocketmq.producer.heartbeatBrokerInterval = 30000
//			caf.mq.rocketmq.producer.maxMessageSize = 4194304
//			caf.mq.rocketmq.producer.persistConsumerOffsetInterval = 5000
//			caf.mq.rocketmq.producer.pollNameServerInterval = 30000
//			caf.mq.rocketmq.producer.retryAnotherBrokerWhenNotStoreOK = false
//			caf.mq.rocketmq.producer.retryTimesWhenSendAsyncFailed = 2
//			caf.mq.rocketmq.producer.retryTimesWhenSendFailed = 2
//			caf.mq.rocketmq.producer.sendLatencyFaultEnable = false
//			caf.mq.rocketmq.producer.sendMessageWithVIPChannel = true
//			caf.mq.rocketmq.producer.sendMsgTimeout = 3000
//			caf.mq.rocketmq.producer.unitMode = false
//			caf.mq.rocketmq.producer.vipChannelEnabled = true

    public RocketMQSimpleProducer(RocketMQProducerConfig producerConfig) {
        super(producerConfig);
        mqProducer = new DefaultMQProducer();
        mqProducer.setNamesrvAddr(producerConfig.getNamesrvAddr());

        // mqProducer.setClientCallbackExecutorThreads(producerConfig.getClientCallbackExecutorThreads());
        mqProducer.setCompressMsgBodyOverHowmuch(producerConfig.getCompressMsgBodyOverHowmuch());
        mqProducer.setDefaultTopicQueueNums(producerConfig.getDefaultTopicQueueNums());
        mqProducer.setHeartbeatBrokerInterval(producerConfig.getHeartbeatBrokerInterval());
        mqProducer.setMaxMessageSize(producerConfig.getMaxMessageSize());
        mqProducer.setPersistConsumerOffsetInterval(producerConfig.getPersistConsumerOffsetInterval());
        mqProducer.setPollNameServerInterval(producerConfig.getPollNameServerInterval());
        mqProducer.setRetryAnotherBrokerWhenNotStoreOK(producerConfig.isRetryAnotherBrokerWhenNotStoreOK());
        mqProducer.setRetryTimesWhenSendAsyncFailed(producerConfig.getRetryTimesWhenSendAsyncFailed());
        mqProducer.setRetryTimesWhenSendFailed(producerConfig.getRetryTimesWhenSendFailed());
        mqProducer.setSendLatencyFaultEnable(producerConfig.isSendLatencyFaultEnable());
        mqProducer.setSendMessageWithVIPChannel(producerConfig.isSendMessageWithVIPChannel());
        mqProducer.setSendMsgTimeout(producerConfig.getSendMsgTimeout());
        // mqProducer.setUnitMode(producerConfig.isUnitMode());
        mqProducer.setVipChannelEnabled(producerConfig.isVipChannelEnabled());

        // mqProducer.setUseTLS(mqProducer.isUseTLS());
        // mqProducer.setInstanceName(mqProducer.getInstanceName());
        // mqProducer.setUnitName(mqProducer.getUnitName());
        // mqProducer.setCreateTopicKey(mqProducer.getCreateTopicKey());
        // mqProducer.setClientIP(mqProducer.getClientIP());
    }

    public RocketMQSimpleProducer(RocketMQProducerConfig producerConfig, String namespace) {
        this(producerConfig);
        this.namespace = namespace;
    }

    @Override
    public void start() throws Exception {
        mqProducer.setProducerGroup(getRocketProducerConfig().getProducerGroup());

        Assert.isTrue(StringUtils.isNotEmpty(mqProducer.getProducerGroup()), "producerGroup must be specified!");
        Assert.isTrue(!RocketMQProducerConfig.DEFAULT_PRODUCER_GROUP.equals(mqProducer.getProducerGroup()),
                "producerGroup must be specified! It is forbided to use the default group!");

        log.info("begin add shutdownHook for rocketmq producer.");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                mqProducer.shutdown();
                log.info("rocketmq producer has shutdown gracefully!");
            } catch (Exception e) {
                log.error("error occurred during rocketmq producer shutdown! pls check! ", e);
            }
        }));
        log.info("rocketmq producer shutdown hook added!");

        mqProducer.start();
    }

    @Override
    public SendResult send(String topic, String tags, String message) {
        Message msg = getMessage(topic, tags, message);
        if (msg == null) {
            return null;
        }
        return super.send(mqProducer, msg, mqProducer.getProducerGroup(), RocketMQSimpleProducer.class.getSimpleName());
    }

    @Override
    public SendResult send(String topic, String message) {
        Message msg = getMessage(topic, message);
        if (msg == null) {
            return null;
        }
        return super.send(mqProducer, msg, mqProducer.getProducerGroup(), RocketMQSimpleProducer.class.getSimpleName());
    }

    @Override
    public void logConfig() {
        log.info(JSON.toJSONString(this.mqProducer, SerializerFeature.PrettyFormat, SerializerFeature.WriteClassName));
    }

    public String getNamespace() {
        return namespace;
    }

    public String getProducerGroup() {
        return mqProducer.getProducerGroup();
    }

    private Message getMessage(String topic, String message) {
        return getMessage(topic, null, message);
    }

    private Message getMessage(String topic, String tags, String message) {
        Message msg = null;
        try {
            if (tags == null) {
                msg = new Message(topic, message.getBytes(RemotingHelper.DEFAULT_CHARSET));
            } else {
                msg = new Message(topic, tags, message.getBytes(RemotingHelper.DEFAULT_CHARSET));
            }
        } catch (Exception e) {
            // don't print log to save disk io.
            // RocketMQProducerExceptionStat.addRocketMQProducerException(e, log);
            log.error(e.getMessage(), e);
        }
        return msg;
    }

}
