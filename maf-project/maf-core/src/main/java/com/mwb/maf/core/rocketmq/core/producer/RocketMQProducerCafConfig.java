package com.mwb.maf.core.rocketmq.core.producer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RocketMQProducerCafConfig {
    /**
     * caf捕获到rocketProducerClient消息发送失败后，尝试重发的次数
     */
    private int cafRetryCountWhenSendFailed = 3;

    /**
     * caf存放重发消息的队列长度，最大不能超过10000
     */
    // private int cafRetryQueueSize = MAX_RETRY_QUEUE_SIZE;
    private int cafRetryQueueSize = 1000;

    private static final int MAX_RETRY_QUEUE_SIZE = 10000;

    /**
     * caf遍历重发消息queue的时间周期，默认是1000毫秒
     */
    private int cafRetryQueueSchedulePeriod = 1000;

    /**
     * 每次消息重发周期内发送的最大消息数
     */
    private int resendMaxCountScheduled = 1000;

    public int getCafRetryCountWhenSendFailed() {
        return cafRetryCountWhenSendFailed;
    }

    public void setCafRetryCountWhenSendFailed(int cafRetryCountWhenSendFailed) {
        this.cafRetryCountWhenSendFailed = cafRetryCountWhenSendFailed;
    }

    public int getCafRetryQueueSize() {
        return cafRetryQueueSize;
    }

    public int getResendMaxCountScheduled() {
        return resendMaxCountScheduled;
    }

    public void setResendMaxCountScheduled(int resendMaxCountScheduled) {
        this.resendMaxCountScheduled = resendMaxCountScheduled;
    }

    public void setCafRetryQueueSize(int cafRetryQueueSize) {
        if (cafRetryQueueSize > MAX_RETRY_QUEUE_SIZE || cafRetryQueueSize <= 100) {
            return;
        }
        this.cafRetryQueueSize = cafRetryQueueSize;
    }

    public int getCafRetryQueueSchedulePeriod() {
        return cafRetryQueueSchedulePeriod;
    }

    public void setCafRetryQueueSchedulePeriod(int cafRetryQueueSchedulePeriod) {
        this.cafRetryQueueSchedulePeriod = cafRetryQueueSchedulePeriod;
    }

    public void logConfig() {
        log.info(JSON.toJSONString(this, SerializerFeature.PrettyFormat, SerializerFeature.WriteClassName));
    }
}
