package com.mwb.maf.core.metrics;

import com.mwb.maf.core.util.TimeUtils;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
@Getter
public class LatencyStat {
    private Gauge min;
    private Gauge max;
    private Gauge concurrent;
    private Counter error;
    private String tag;
    private Histogram histogram;

    public LatencyStat(Histogram histogram, Gauge min, Gauge max, Gauge concurrent, Counter error, String tag) {
        this.histogram = histogram;
        this.concurrent = concurrent;
        this.min = min;
        this.max = max;
        this.error = error;
        this.tag = tag;
    }

    public void error(String metrics, String category) {
        error.labels(metrics, category).inc();
    }

    public void error(String... labels) {
        error.labels(labels).inc();
    }

    public void observe(String metrics, String category, long value, TimeUnit timeUnit) {
        final double elapsedSeconds = TimeUtils.elapsedSeconds(timeUnit.toNanos(value));
        setMax(metrics, category, elapsedSeconds);
        setMin(metrics, category, elapsedSeconds);
        histogram.labels(metrics, category).observe(elapsedSeconds);
    }

    public void observe(long value, TimeUnit timeUnit, String... labels) {
        final double elapsedSeconds = TimeUtils.elapsedSeconds(timeUnit.toNanos(value));
        setMax(elapsedSeconds, labels);
        setMin(elapsedSeconds, labels);
        histogram.labels(labels).observe(elapsedSeconds);
    }

    private void setMax(String metrics, String category, double elapsedSeconds) {
        final Gauge.Child maxWithLabels = max.labels(metrics, category);
        if (elapsedSeconds > maxWithLabels.get()) {
            maxWithLabels.set(elapsedSeconds);
        }
    }

    private void setMax(double elapsedSeconds, String... labels) {
        final Gauge.Child maxWithLabels = max.labels(labels);
        if (elapsedSeconds > maxWithLabels.get()) {
            maxWithLabels.set(elapsedSeconds);
        }
    }

    private void setMin(String metrics, String category, double elapsedSeconds) {
        final Gauge.Child minWithLabels = min.labels(metrics, category);
        if (elapsedSeconds < minWithLabels.get() || minWithLabels.get() == 0D) {
            minWithLabels.set(elapsedSeconds);
        }
    }

    private void setMin(double elapsedSeconds, String... labels) {
        final Gauge.Child minWithLabels = min.labels(labels);
        if (elapsedSeconds < minWithLabels.get() || minWithLabels.get() == 0D) {
            minWithLabels.set(elapsedSeconds);
        }
    }

    public Timer startTimer(String metrics, String category) {
        return new Timer(metrics, category);
    }

    public void inc(String metrics, String category) {
        concurrent.labels(metrics, category).inc();
    }

    public void inc(String... labels) {
        concurrent.labels(labels).inc();
    }

    public void dec(String metrics, String category) {
        concurrent.labels(metrics, category).dec();
    }

    public void dec(String... labels) {
        concurrent.labels(labels).dec();
    }

    public void reset() {
        min.clear();
        max.clear();
    }

    public class Timer {
        final String metrics;
        final String category;
        final Histogram.Timer timer;
        final String[] labels;

        public Timer(String... labels) {
            this.metrics = (labels != null && labels.length > 0) ? labels[0] : null;
            this.category = (labels != null && labels.length > 1) ? labels[1] : null;
            this.labels = labels;
            this.timer = histogram.labels(labels).startTimer();
        }

        public Timer(String metrics, String category) {
            this.labels = new String[]{metrics, category};
            this.metrics = metrics;
            this.category = category;
            this.timer = histogram.labels(metrics, category).startTimer();
        }

        public double observeDuration() {
            final double observeDuration = timer.observeDuration();
            LatencyStat.this.setMin(metrics, category, observeDuration);
            LatencyStat.this.setMax(metrics, category, observeDuration);
            return observeDuration;
        }

        public double observeDurationFromLabels() {
            final double observeDuration = timer.observeDuration();
            LatencyStat.this.setMin(observeDuration, labels);
            LatencyStat.this.setMax(observeDuration, labels);
            return observeDuration;
        }
    }
}
