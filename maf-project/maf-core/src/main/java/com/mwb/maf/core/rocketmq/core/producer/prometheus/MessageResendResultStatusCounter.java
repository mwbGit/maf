package com.mwb.maf.core.rocketmq.core.producer.prometheus;

import io.prometheus.client.Counter;

//7.计算消息重发返回的resultStatus的统计，每天00:00点reset=0
public class MessageResendResultStatusCounter {

    private static final Counter MESSAGE_RESEND_RESULT_STATUS_COUNTER = Counter.build()
            .name("rocketmq_message_resend_result_status_counter")
            .help("the count of message first send result status.")
            .labelNames("topic", "producerGroup", "clazz", "pid", "status").register();

    public static void inc(String topic, String producerGroup, String clazzName, String pid, String status) {
        MESSAGE_RESEND_RESULT_STATUS_COUNTER.labels(topic, producerGroup, clazzName, pid, status).inc();
    }

    public static void clear() {
        MESSAGE_RESEND_RESULT_STATUS_COUNTER.clear();
    }

}
