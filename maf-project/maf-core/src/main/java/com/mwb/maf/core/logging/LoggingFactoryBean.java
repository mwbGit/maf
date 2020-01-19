package com.mwb.maf.core.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

import java.util.Queue;
import java.util.concurrent.*;

/**
 * 描述:
 *
 * @author mengweibo@kanzhun.com
 * @create 2020/1/16
 */
public class LoggingFactoryBean implements ApplicationContextAware, InitializingBean {
    private static Logger logger = LoggerFactory.getLogger(LoggingNoticeListener.class);

    private ExecutorService executorService = new ThreadPoolExecutor(1, 2, 1, TimeUnit.MINUTES, new ArrayBlockingQueue<>(8192), new ThreadPoolExecutor.DiscardPolicy());

    private static Queue<LoggingNoticeEvent> EVENT_QUEUE = new ConcurrentLinkedQueue<>();

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext initApplicationContext) throws BeansException {
        Assert.notNull(initApplicationContext, "ApplicationContext must not be null");
        applicationContext = initApplicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static void addPublishEvent(LoggingNoticeEvent event) {
        EVENT_QUEUE.add(event);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        executorService.execute(() -> {
            while (true) {
                if (EVENT_QUEUE.isEmpty()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        logger.error("push execute sleep err", e);
                    }
                } else {
                    LoggingNoticeEvent event = EVENT_QUEUE.poll();
                    if (event != null) {
                        applicationContext.publishEvent(event);
                    }
                }
            }
        });
    }
}
