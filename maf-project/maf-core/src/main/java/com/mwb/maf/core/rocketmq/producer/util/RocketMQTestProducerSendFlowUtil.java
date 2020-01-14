package com.mwb.maf.core.rocketmq.producer.util;

import com.mwb.maf.core.rocketmq.core.producer.prometheus.MessageFirstSendResultStatusCounter;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

public class RocketMQTestProducerSendFlowUtil {

    public static SendResult test(Message message, String producerGroup, String clazzName, String currentPID)
            throws MQClientException, RemotingException, MQBrokerException, InterruptedException, Exception {
        int factor = (int) (Math.random() * 10);
        SendResult sr = new SendResult();
        switch (factor) {
            case 0:
                sr.setSendStatus(SendStatus.FLUSH_DISK_TIMEOUT);
                // 第一发送消息的返回状态统计项。
                MessageFirstSendResultStatusCounter.inc(message.getTopic(), producerGroup, clazzName, currentPID,
                        sr.getSendStatus().name());
                return sr;
            case 1:
                sr.setSendStatus(SendStatus.FLUSH_SLAVE_TIMEOUT);
                // 第一发送消息的返回状态统计项。
                MessageFirstSendResultStatusCounter.inc(message.getTopic(), producerGroup, clazzName, currentPID,
                        sr.getSendStatus().name());
                return sr;
            case 2:
                sr.setSendStatus(SendStatus.SLAVE_NOT_AVAILABLE);
                // 第一发送消息的返回状态统计项。
                MessageFirstSendResultStatusCounter.inc(message.getTopic(), producerGroup, clazzName, currentPID,
                        sr.getSendStatus().name());
                return sr;
            case 3:
                throw new MQClientException(101, "MQClientException");
            case 4:
                throw new RemotingException("RemotingException");
            case 5:
                throw new MQBrokerException(201, "MQBrokerException");
            case 6:
                throw new InterruptedException("InterruptedException");
            case 7:
                throw new Exception("Exception");
        }
        return null;
    }
}
