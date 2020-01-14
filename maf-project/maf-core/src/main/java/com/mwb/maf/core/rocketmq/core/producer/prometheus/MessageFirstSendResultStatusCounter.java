package com.mwb.maf.core.rocketmq.core.producer.prometheus;

import io.prometheus.client.Counter;

//6.计算消息第一次发送返回的resultStatus的统计，每天00:00点reset=0
public class MessageFirstSendResultStatusCounter {

    private static final Counter MESSAGE_FIRST_SEND_RESULT_STATUS_COUNTER = Counter.build()
            .name("rocketmq_message_first_send_result_status_counter")
            .help("the count of message first send result status.")
            .labelNames("topic", "producerGroup", "clazz", "pid", "status").register();

    public static void inc(String topic, String producerGroup, String clazzName, String pid, String status) {
        MESSAGE_FIRST_SEND_RESULT_STATUS_COUNTER.labels(topic, producerGroup, clazzName, pid, status).inc();
    }

    public static void clear() {
        MESSAGE_FIRST_SEND_RESULT_STATUS_COUNTER.clear();
    }

}
