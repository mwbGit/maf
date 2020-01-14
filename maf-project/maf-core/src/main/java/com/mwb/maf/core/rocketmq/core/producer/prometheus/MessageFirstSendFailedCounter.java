package com.mwb.maf.core.rocketmq.core.producer.prometheus;

import io.prometheus.client.Counter;

//1.计算消息第一次发送失败的次数，每天00:00点reset=0
public class MessageFirstSendFailedCounter {

    private static final Counter MESSAGE_FIRST_SEND_FAILED_COUNTER = Counter.build()
            .name("rocketmq_message_first_send_failed_counter").help("the count of message first send failed.")
            .labelNames("topic", "producerGroup", "clazz", "failedException", "failedCode", "pid").register();

    public static void inc(String topic, String producerGroup, String clazzName, String failedException,
                           String failedCode, String pid) {
        MESSAGE_FIRST_SEND_FAILED_COUNTER.labels(topic, producerGroup, clazzName, failedException, failedCode, pid)
                .inc();
    }

    public static void clear() {
        MESSAGE_FIRST_SEND_FAILED_COUNTER.clear();
    }

}
