package com.mwb.maf.core.rocketmq.core.producer.prometheus;

import io.prometheus.client.Counter;

//4.计算消息重发失败的次数，每天00:00点reset=0
public class MessageResendFailedTotalCounter {

    private static final Counter MESSAGE_RESEND_FAILED_TOTAL_COUNTER = Counter.build()
            .name("rocketmq_message_resend_failed_total_counter").help("the count of message resend failed total.")
            .labelNames("topic", "producerGroup", "clazz", "failedException", "failedCode", "pid").register();

    public static void inc(String topic, String producerGroup, String clazzName, String failedException,
                           String failedCode, String pid) {
        MESSAGE_RESEND_FAILED_TOTAL_COUNTER.labels(topic, producerGroup, clazzName, failedException, failedCode, pid)
                .inc();
    }

    public static void clear() {
        MESSAGE_RESEND_FAILED_TOTAL_COUNTER.clear();
    }

}
