package com.mwb.maf.core.rocketmq.core.consumer;

import com.mwb.maf.core.metrics.LatencyProfiler;
import com.mwb.maf.core.metrics.LatencyStat;
import com.mwb.maf.core.util.PIDUtil;

import java.util.concurrent.TimeUnit;

public class RocketMQConsumerProfiler {

    private static final LatencyStat ROCKETMQ_CONSUMER_PROCESS_COUNT_STAT = LatencyProfiler.Builder.build()
            .tag("rocketmq").name("rocketmq_consumer_process").labelNames("topic", "consumerGroup", "clazz", "pid")
            .buckets(0.000, 0.001, 0.002, 0.003, 0.004, 0.005, 0.006, 0.007, 0.008, 0.009, 0.010, 0.02, 0.05, 0.1, 0.3,
                    0.5, 0.7, 0.9, 1, 3, 5, 10, 20, 30, 100, 1000, 1000 * 10)
            .create();

    private static final LatencyStat ROCKETMQ_CONSUMER_B2C_COUNT_STAT = LatencyProfiler.Builder.build().tag("rocketmq")
            .name("rocketmq_consumer_b2c").labelNames("topic", "consumerGroup", "clazz", "pid")
            .buckets(0.000, 0.001, 0.002, 0.003, 0.004, 0.005, 0.006, 0.007, 0.008, 0.009, 0.010, 0.02, 0.05, 0.1, 0.3,
                    0.5, 0.7, 0.9, 1, 3, 5, 10, 20, 30, 100, 1000, 1000 * 10, 1000 * 60, 1000 * 60 * 10, 1000 * 60 * 20,
                    1000 * 60 * 30, 1000 * 60 * 60, 1000 * 60 * 60 * 12, 1000 * 60 * 60 * 24, 1000 * 60 * 60 * 24 * 2,
                    1000 * 60 * 60 * 24 * 3, 1000 * 60 * 60 * 24 * 4, 1000 * 60 * 60 * 24 * 5)
            .create();

    private static final LatencyStat ROCKETMQ_CONSUMER_S2C_COUNT_STAT = LatencyProfiler.Builder.build().tag("rocketmq")
            .name("rocketmq_consumer_s2c").labelNames("topic", "consumerGroup", "clazz", "pid")
            .buckets(0.000, 0.001, 0.002, 0.003, 0.004, 0.005, 0.006, 0.007, 0.008, 0.009, 0.010, 0.02, 0.05, 0.1, 0.3,
                    0.5, 0.7, 0.9, 1, 3, 5, 10, 20, 30, 100, 1000, 1000 * 10, 1000 * 60, 1000 * 60 * 10, 1000 * 60 * 20,
                    1000 * 60 * 30, 1000 * 60 * 60, 1000 * 60 * 60 * 12, 1000 * 60 * 60 * 24, 1000 * 60 * 60 * 24 * 2,
                    1000 * 60 * 60 * 24 * 3, 1000 * 60 * 60 * 24 * 4, 1000 * 60 * 60 * 24 * 5)
            .create();

    private static String currentPID;

    public static Processor beginProcessor(String topic, String consumerGroup, Class<?> clz, long msgB2C, long msgS2C) {
        currentPID = PIDUtil.getProcessID();
        return new Processor(topic, consumerGroup, clz.getName(), msgB2C, msgS2C);
    }

    public static class Processor {
        private final String topic;
        private final String consumerGroup;
        private final String clazz;
        // private final LatencyStat.Timer timer;

        private final long msgB2C;
        private final long msgS2C;

        private final long processStartTime;

        Processor(String topic, String consumerGroup, String clazz, long msgB2C, long msgS2C) {
            this.topic = topic;
            this.consumerGroup = consumerGroup;
            this.clazz = clazz;
            this.msgB2C = msgB2C;
            this.msgS2C = msgS2C;

            // timer =
            // ROCKETMQ_CONSUMER_PROCESS_COUNT_STAT.startTimer(this.topicAndConsumerGroup,
            // this.clazz);

            processStartTime = System.currentTimeMillis();

            ROCKETMQ_CONSUMER_PROCESS_COUNT_STAT.inc(this.topic, this.consumerGroup, this.clazz, currentPID);

            ROCKETMQ_CONSUMER_B2C_COUNT_STAT.inc(this.topic, this.consumerGroup, this.clazz, currentPID);
            ROCKETMQ_CONSUMER_S2C_COUNT_STAT.inc(this.topic, this.consumerGroup, this.clazz, currentPID);
        }

        public String getClazz() {
            return clazz;
        }

        public void exception() {
            ROCKETMQ_CONSUMER_PROCESS_COUNT_STAT.error(this.topic, this.consumerGroup, this.clazz, currentPID);
            ROCKETMQ_CONSUMER_B2C_COUNT_STAT.error(this.topic, this.consumerGroup, this.clazz, currentPID);
            ROCKETMQ_CONSUMER_S2C_COUNT_STAT.error(this.topic, this.consumerGroup, this.clazz, currentPID);
        }

        public void complete() {
            long cts = System.currentTimeMillis();

            // timer.observeDuration();
            ROCKETMQ_CONSUMER_PROCESS_COUNT_STAT.observe(cts - processStartTime, TimeUnit.MILLISECONDS, this.topic,
                    this.consumerGroup, this.clazz, currentPID);
            ROCKETMQ_CONSUMER_PROCESS_COUNT_STAT.dec(this.topic, this.consumerGroup, this.clazz, currentPID);

//			System.out.println("hahaha:msgB2C:" + this.msgB2C + ",msgS2C:" + this.msgS2C + "," + "processStartTime:"
//					+ processStartTime + ",cts:" + cts);

            ROCKETMQ_CONSUMER_B2C_COUNT_STAT.observe((cts - this.msgB2C), TimeUnit.MILLISECONDS, this.topic,
                    this.consumerGroup, this.clazz, currentPID);
            ROCKETMQ_CONSUMER_B2C_COUNT_STAT.dec(this.topic, this.consumerGroup, this.clazz, currentPID);

            ROCKETMQ_CONSUMER_S2C_COUNT_STAT.observe((cts - this.msgS2C), TimeUnit.MILLISECONDS, this.topic,
                    this.consumerGroup, this.clazz, currentPID);
            ROCKETMQ_CONSUMER_S2C_COUNT_STAT.dec(this.topic, this.consumerGroup, this.clazz, currentPID);

        }
    }

    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis());
        System.out.println(System.nanoTime());
    }
}
