package com.mwb.maf.core.rocketmq.producer;

import com.mwb.maf.core.rocketmq.core.producer.RocketMQProducerCafConfig;
import com.mwb.maf.core.rocketmq.core.producer.prometheus.*;
import com.mwb.maf.core.util.PIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.ThreadFactoryImpl;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.concurrent.*;

@Slf4j
public class RocketMQRestrySendMessageScheduler {

    private final static ScheduledExecutorService messageReSendFastFailureExecutorService = Executors
            .newSingleThreadScheduledExecutor(new ThreadFactoryImpl("MessageReSendFastFailureScheduledThread"));

    private final static ScheduledExecutorService clearMessageProduceStatExecutorService = Executors
            .newSingleThreadScheduledExecutor(new ThreadFactoryImpl("ClearMessageProduceStatScheduledThread"));

    private static BlockingQueue<RocketMQRestrySendMessage> waitToSendAgainMessageQueue;

    private final static String LOG_PREFIX_MSG_RESEND_OK = "MSG-RESEND-OK: ";

    private final static String LOG_PREFIX_MSG_RESEND_FAILED = "MSG-RESEND-FAILED: ";

    private static volatile boolean STARTED = false;

    private static String currentPID;

    private static RocketMQProducerCafConfig producerCommonConfig;

    public static void start(RocketMQProducerCafConfig producerCommonConfig) {
        if (!STARTED) {
            synchronized (RocketMQRestrySendMessageScheduler.class) {
                if (!STARTED) {
                    if (producerCommonConfig != null) {
                        RocketMQRestrySendMessageScheduler.producerCommonConfig = producerCommonConfig;
                        waitToSendAgainMessageQueue = new LinkedBlockingDeque<RocketMQRestrySendMessage>(
                                producerCommonConfig.getCafRetryQueueSize());
                    } else {
                        RocketMQRestrySendMessageScheduler.producerCommonConfig = new RocketMQProducerCafConfig();
                        waitToSendAgainMessageQueue = new LinkedBlockingDeque<RocketMQRestrySendMessage>(10000);
                    }

                    currentPID = PIDUtil.getProcessID();

                    startResendThreadPool();
                    startClearProduceCounter();

                    STARTED = true;
                }
            }
        }
    }

    private static void startResendThreadPool() {
        messageReSendFastFailureExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    processRetrySendMessage();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }, 1000, producerCommonConfig.getCafRetryQueueSchedulePeriod(), TimeUnit.MILLISECONDS);
    }

    private static void startClearProduceCounter() {
        clearMessageProduceStatExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    MessageFinalSendFailedTotalCounter.clear();
                    MessageFirstSendFailedCounter.clear();
                    MessageResendFailedTotalCounter.clear();
                    MessageFirstSendResultStatusCounter.clear();
                    MessageResendResultStatusCounter.clear();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }, 1, 1, TimeUnit.HOURS);
    }

    static void addWaitToResendQueue(RocketMQRestrySendMessage restrySendMessage) {
        try {
            waitToSendAgainMessageQueue.add(restrySendMessage);
            // 重发队列长度计数器+1
            MessageWaitResendGauge.inc(restrySendMessage.getTopic(), restrySendMessage.getProducerGroup(),
                    restrySendMessage.getClazzName(), currentPID);
        } catch (Exception e) {
            // don't print log to save io.
            // RocketMQProducerExceptionStat.addRocketMQProducerException(e, log);
            log.error(e.getMessage(), e);

            // 消息最终发送失败的次数+1,发送彻底失败
            MessageFinalSendFailedTotalCounter.inc(restrySendMessage.getTopic(), restrySendMessage.getProducerGroup(),
                    restrySendMessage.getClazzName(), "IntoResendQueueException", "None", currentPID);
        }
    }

    private static void processRetrySendMessage() {
        int index = 0;
        while (true) {

            RocketMQRestrySendMessage message = waitToSendAgainMessageQueue.poll();
            if (message == null) {
                break;
            }

            // 重发队列长度计数器-1
            MessageWaitResendGauge.dec(message.getTopic(), message.getProducerGroup(), message.getClazzName(),
                    currentPID);

            // 一次循环最多处理的消息个数
            if (index > producerCommonConfig.getResendMaxCountScheduled()) {
                break;
            }
            index++;

            try {
                // test code: test exception.
//				SendResult testSR = RocketMQTestProducerSendFlowUtil.test(message.getMessage(),
//						message.getProducerGroup(), message.getClazzName(), currentPID);
//				if (testSR != null) {
//					continue;
//				}

                SendResult sendResult = message.getMqProducer().send(message.getMessage());

                // 消息重发返回的状态统计
                MessageResendResultStatusCounter.inc(message.getTopic(), message.getProducerGroup(),
                        message.getClazzName(), currentPID, sendResult.getSendStatus().name());

                log.info(new StringBuilder().append(LOG_PREFIX_MSG_RESEND_OK)
                        .append(message.getFirstRetrySendTimeStamp()).append(" ")
                        .append(message.getLastRetrySendTimeStamp()).toString());

            } catch (MQClientException e) {
                processWhenResendException(message, e, "rocketmq-MQClientException", e.getResponseCode() + "");
            } catch (RemotingException e) {
                processWhenResendException(message, e, "rocketmq-RemotingException", "None");
            } catch (MQBrokerException e) {
                processWhenResendException(message, e, "rocketmq-MQBrokerException", e.getResponseCode() + "");
            } catch (InterruptedException e) {
                processWhenResendException(message, e, "rocketmq-InterruptedException", "None");
            } catch (Exception e) {
                processWhenResendException(message, e, "rocketmq-Exception", "None");
            }
        }
    }

    private static void processWhenResendException(RocketMQRestrySendMessage message, Exception e,
                                                   String failedException, String failedCode) {
        // don't print log to save disk io.
        // RocketMQProducerExceptionStat.addRocketMQProducerException(e, log);
        log.error(e.getMessage(), e);

        // 消息重发失败的总次数
        MessageResendFailedTotalCounter.inc(message.getTopic(), message.getProducerGroup(), message.getClazzName(),
                failedException, failedCode, currentPID);

        // 将发送失败的消息加入重发队列
        // don't print log to save disk io.
        int retryCount = message.getRetryCount();
        // 默认重发3次
        if (retryCount > producerCommonConfig.getCafRetryCountWhenSendFailed()) {

            log.error(new StringBuilder().append(LOG_PREFIX_MSG_RESEND_FAILED)
                    .append(message.getFirstRetrySendTimeStamp()).append(" ")
                    .append(message.getLastRetrySendTimeStamp()).toString());
            // 消息最终发送失败的次数+1,发送彻底失败
            MessageFinalSendFailedTotalCounter.inc(message.getTopic(), message.getProducerGroup(),
                    message.getClazzName(), "OverResendMaxException", "None", currentPID);

        } else {
            message.setRetryCount(retryCount + 1);
            message.setLastRetrySendTimeStamp(System.currentTimeMillis());
            addWaitToResendQueue(message);
        }

    }
}
