package com.mwb.maf.core.rocketmq.producer;

import lombok.Getter;
import lombok.Setter;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.common.message.Message;

@Setter
@Getter
public class RocketMQRestrySendMessage {

    private MQProducer mqProducer;

    private Message message;

    private String topic;

    private String producerGroup;

    private String clazzName;

    private int retryCount = 1;

    private long firstRetrySendTimeStamp;

    private long lastRetrySendTimeStamp;

    public RocketMQRestrySendMessage(MQProducer mqProducer, Message message, String producerGroup, String clazzName) {
        this.mqProducer = mqProducer;
        this.message = message;
        this.firstRetrySendTimeStamp = System.currentTimeMillis();
        this.lastRetrySendTimeStamp = this.firstRetrySendTimeStamp;

        this.topic = message.getTopic();
        this.producerGroup = producerGroup;
        this.clazzName = clazzName;
    }

}
