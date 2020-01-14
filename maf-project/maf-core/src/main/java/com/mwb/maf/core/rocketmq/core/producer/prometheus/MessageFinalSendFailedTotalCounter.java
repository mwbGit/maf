package com.mwb.maf.core.rocketmq.core.producer.prometheus;

import io.prometheus.client.Counter;

//5.计算消息最终发送失败的次数，每天00:00点reset=0
// 两类：
//重发队列超过上限后丢弃;
//重发次数超过上限;
public class MessageFinalSendFailedTotalCounter {

    private static final Counter MESSAGE_FINAL_SEND_FAILED_TOTAL_COUNTER = Counter.build()
            .name("rocketmq_message_final_send_failed_total_counter").help("the count of message resend failed total.")
            .labelNames("topic", "producerGroup", "clazz", "failedException", "failedCode", "pid").register();

    public static void inc(String topic, String producerGroup, String clazzName, String failedException,
                           String failedCode, String pid) {
        MESSAGE_FINAL_SEND_FAILED_TOTAL_COUNTER
                .labels(topic, producerGroup, clazzName, failedException, failedCode, pid).inc();
    }

    public static void clear() {
        MESSAGE_FINAL_SEND_FAILED_TOTAL_COUNTER.clear();
    }

}
