package com.mwb.maf.core.base;

import com.mwb.maf.core.logging.Loggers;
import com.mwb.maf.core.metrics.LatencyStatPrinter;
import com.mwb.maf.core.metrics.MonitorConfig;
import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.DefaultExports;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@ConditionalOnClass({
        ApplicationListener.class,
        ApplicationEvent.class,
        ApplicationReadyEvent.class
})
@Slf4j
public class SpringApplicationEventListenerAutoConfiguration implements ApplicationListener<ApplicationEvent> {
    private static final Logger logger = Loggers.getFrameworkLogger();
    private static final ScheduledExecutorService EXECUTOR = Executors.newSingleThreadScheduledExecutor(new CThreadFactory());
    @Autowired
    private MonitorConfig monitorConfig;
    @Autowired
    private WarmUpRegistry warmUpRegistry;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationReadyEvent) {
            logger.info(StringUtils.rightPad("** MAF START " + event.getClass().getSimpleName() + " ", 150, '*'));
            bootstrapMonitor();
            warmUp();
        } else if (event instanceof ApplicationFailedEvent) {
            logger.info(StringUtils.rightPad("** MAF START FAILED " + event.getClass().getSimpleName() + " ", 150, '*'));
        } else if (event instanceof ContextClosedEvent) {
            logger.info(StringUtils.rightPad("** MAF STOPPED " + event.getClass().getSimpleName() + " ", 150, '*'));
        } else if (StringUtils.containsAny(event.getClass().getSimpleName(), "Application", "Context")) {
            logger.info("{} - {}", "** MAF " + ApplicationListener.class.getSimpleName(), event.getClass().getSimpleName());
        }
    }

    private void bootstrapMonitor() {
        try {
            DefaultExports.initialize();
            new HTTPServer(monitorConfig.getPort(), true);
            log.info("{} started on port {}", "Prometheus HttpServer", monitorConfig.getPort());
            EXECUTOR.scheduleAtFixedRate(
                    new LatencyStatPrinter(monitorConfig),
                    monitorConfig.getLogDelay(),
                    monitorConfig.getLogPeriod(),
                    TimeUnit.SECONDS
            );
        } catch (IOException e) {
            log.error("init Prometheus HTTPServer error!", e);
        }
    }

    private void warmUp() {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        final Thread thread = new Thread(() -> {
            for (WarmUp warmUp : warmUpRegistry.warmUps()) {
                long begin = System.currentTimeMillis();
                final WarmUpResult warmUpResult = warmUp.warmUp();
                Loggers.getFrameworkLogger().info("warmUp -> {}, cost: {}ms", warmUpResult, System.currentTimeMillis() - begin);
            }
            countDownLatch.countDown();
        });
        thread.start();
        try {
            countDownLatch.await(60, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {

        } finally {
            WarmUpResult.markAsDone();
        }
    }
}
