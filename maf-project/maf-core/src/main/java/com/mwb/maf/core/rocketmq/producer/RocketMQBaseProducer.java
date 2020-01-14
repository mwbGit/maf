package com.mwb.maf.core.rocketmq.producer;

import com.mwb.maf.core.rocketmq.core.producer.RocketMQProducerConfig;
import com.mwb.maf.core.rocketmq.core.producer.prometheus.MessageFirstSendFailedCounter;
import com.mwb.maf.core.rocketmq.core.producer.prometheus.MessageFirstSendResultStatusCounter;
import com.mwb.maf.core.util.PIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

@Slf4j
public abstract class RocketMQBaseProducer {

    private RocketMQProducerConfig producerConfig;

    private String currentPID;

    public RocketMQBaseProducer(RocketMQProducerConfig producerConfig) {
        this.producerConfig = producerConfig;
        currentPID = PIDUtil.getProcessID();
    }

    protected RocketMQProducerConfig getRocketProducerConfig() {
        return producerConfig;
    }

    public abstract void start() throws Exception;

    public abstract SendResult send(String topic, String tags, String message);

    public abstract SendResult send(String topic, String message);

    public abstract void logConfig();

    SendResult send(MQProducer producer, Message message, String producerGroup, String clazzName) {
        try {
            // test code: test exception.
//			SendResult testSR = RocketMQTestProducerSendFlowUtil.test(message, producerGroup, clazzName, currentPID);
//			if (testSR != null) {
//				return testSR;
//			}

            SendResult sendResult = producer.send(message);
            // 第一发送消息的返回状态统计项。
            MessageFirstSendResultStatusCounter.inc(message.getTopic(), producerGroup, clazzName, currentPID,
                    sendResult.getSendStatus().name());
            return sendResult;
        } catch (MQClientException e) {
            processWhenException(producer, message, e, message.getTopic(), producerGroup, clazzName,
                    "rocketmq-MQClientException", e.getResponseCode() + "");
        } catch (RemotingException e) {
            processWhenException(producer, message, e, message.getTopic(), producerGroup, clazzName,
                    "rocketmq-RemotingException", "None");
        } catch (MQBrokerException e) {
            processWhenException(producer, message, e, message.getTopic(), producerGroup, clazzName,
                    "rocketmq-MQBrokerException", e.getResponseCode() + "");
        } catch (InterruptedException e) {
            processWhenException(producer, message, e, message.getTopic(), producerGroup, clazzName,
                    "rocketmq-InterruptedException", "None");
        } catch (Exception e) {
            processWhenException(producer, message, e, message.getTopic(), producerGroup, clazzName,
                    "rocketmq-Exception", "None");
        }
        return null;
    }

    private void processWhenException(MQProducer producer, Message message, Exception e, String topic,
                                      String producerGroup, String clazzName, String failedException, String failedCode) {
        // don't print log to save disk io.
        // RocketMQProducerExceptionStat.addRocketMQProducerException(e, log);
        log.error(e.getMessage(), e);

        // 将发送失败的消息加入重发队列
        RocketMQRestrySendMessageScheduler
                .addWaitToResendQueue(new RocketMQRestrySendMessage(producer, message, producerGroup, clazzName));
        // 消息第一次发送失败的统计信息加1
        MessageFirstSendFailedCounter.inc(topic, producerGroup, clazzName, failedException, failedCode, currentPID);
    }

    public void setProducerGroup(String producerGroup) {
        producerConfig.setProducerGroup(producerGroup);
    }

}
