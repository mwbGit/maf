package com.mwb.maf.core.rocketmq.producer;

import com.mwb.maf.core.rocketmq.producer.util.RocketMQExceptionStackTraceHashUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.rocketmq.common.ThreadFactoryImpl;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class RocketMQProducerExceptionStat {

    private final static ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(
            new ThreadFactoryImpl("RocketMQProducerSendMessageExceptionStatScheduledThread"));

    private final static Map<Long, RocketMQProducerException> exceptionMap = new ConcurrentHashMap<Long, RocketMQProducerException>();

    private final static String UNIQ_EXCEPTION_OVER_LIMIT = "exceptionMap size has arrived at limit: 100; ";
    private final static Long HASH_UNIQ_EXCEPTION_OVER_LIMIT = RocketMQExceptionStackTraceHashUtil
            .hash(UNIQ_EXCEPTION_OVER_LIMIT);

    private static volatile boolean STARTED = false;

    // 暂时停用
    @Deprecated
    private static void addRocketMQProducerException(Exception e, org.slf4j.Logger log) {
        String fullStackTrace = ExceptionUtils.getStackTrace(e);
        Long hash = RocketMQExceptionStackTraceHashUtil.hash(fullStackTrace);

        if (exceptionMap.containsKey(hash)) {
            exceptionMap.get(hash).count.incrementAndGet();
        } else {
            if (exceptionMap.size() > 10) {
                // print exception detail to disk file. this thing almost not happen.
                log.error(e.getMessage(), e);

                statIgnoreExceptionCount(e);
                return;
            }
            RocketMQProducerException wrapE = exceptionMap.putIfAbsent(hash,
                    new RocketMQProducerException(e.getMessage(), fullStackTrace));
            if (wrapE != null) {
                wrapE.count.incrementAndGet();
            }
        }
    }

    @Deprecated
    private static void start() {
        if (!STARTED) {
            synchronized (RocketMQProducerExceptionStat.class) {
                if (!STARTED) {
                    scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
                        @Override
                        public void run() {
                            Set<Long> keySet = exceptionMap.keySet();

                            StringBuilder hashesBuilder = new StringBuilder();
                            StringBuilder stackTracesBuilder = new StringBuilder();

                            int count = 1;
                            for (Long hash : keySet) {
                                RocketMQProducerException wrape = exceptionMap.get(hash);

                                hashesBuilder.append("index-").append(count).append(": ").append(hash)
                                        .append(", count: ").append(wrape.getCount().get());
                                stackTracesBuilder.append("index-").append(count).append(": ").append(hash).append("\n")
                                        .append(wrape.getFullStackTrace());

                                // reset
                                wrape.count.set(0l);

                                count++;
                            }

                            log.info(hashesBuilder.append("\n").append(stackTracesBuilder).toString());
                        }
                    }, 1000, 5, TimeUnit.MINUTES);
                    STARTED = true;
                }
            }
        }
    }

    static void statIgnoreExceptionCount(Exception e) {
        RocketMQProducerException wrapE = exceptionMap.putIfAbsent(HASH_UNIQ_EXCEPTION_OVER_LIMIT,
                new RocketMQProducerException(UNIQ_EXCEPTION_OVER_LIMIT, null));
        if (wrapE != null) {
            wrapE.count.incrementAndGet();
        }
    }

    static class RocketMQProducerException {

        private AtomicLong count;

        private String fullStackTrace;

        private String message;

        public RocketMQProducerException(String message, String fullStackTrace) {
            this.message = message;
            this.fullStackTrace = fullStackTrace;
        }

        public AtomicLong getCount() {
            return count;
        }

        public String getFullStackTrace() {
            return fullStackTrace;
        }

        public String getMessage() {
            return message;
        }

    }
}
