package com.mwb.maf.core.rocketmq.core.producer.prometheus;

import io.prometheus.client.Gauge;

//2.计算等待重发的消息个数Gauge
public class MessageWaitResendGauge {

    private static final Gauge MESSAGE_WAIT_RESEND_GAUGE = Gauge.build()
            .name("rocketmq_producer_message_wait_resend_gauge")
            .help("waited resend's message numbers that send failed.")
            .labelNames("topic", "producerGroup", "clazz", "pid").register();

    public static void inc(String topic, String producerGroup, String clazzName, String pid) {
        MESSAGE_WAIT_RESEND_GAUGE.labels(topic, producerGroup, clazzName, pid).inc();
    }

    public static void dec(String topic, String producerGroup, String clazzName, String pid) {
        MESSAGE_WAIT_RESEND_GAUGE.labels(topic, producerGroup, clazzName, pid).dec();
    }

    public static void clear() {
        MESSAGE_WAIT_RESEND_GAUGE.clear();
    }

}
