package com.mwb.maf.core.metrics;

import com.google.common.collect.Sets;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import org.springframework.util.Assert;

import java.util.Set;

public class LatencyProfiler {
    static final Set<LatencyStat> PROFILER_STAT_SET = Sets.newConcurrentHashSet();

    public static class Builder {
        private String tag;
        private String name;
        private String help;
        String[] labelNames = new String[]{};
        private double[] buckets;

        public static Builder build() {
            return new Builder();
        }

        public Builder name(String name) {
            this.name = name;
            this.help = name;
            return this;
        }

        public Builder help(String help) {
            this.help = help;
            return this;
        }

        public Builder tag(String tag) {
            this.tag = tag;
            return this;
        }

        public Builder buckets(double... buckets) {
            this.buckets = buckets;
            return this;
        }

        public Builder labelNames(String... labelNames) {
            this.labelNames = labelNames;
            return this;
        }

        public Builder defineLabels(String metrics, String category) {
            return labelNames(metrics, category);
        }

        public LatencyStat create() {
            Assert.notNull(name, String.format("%s can not be null!", "name"));
            Assert.notNull(tag, String.format("%s can not be null!", "tag"));
            Assert.notNull(help, String.format("%s can not be null!", "help"));
            Assert.isTrue(labelNames != null && labelNames.length > 0, String.format("%s can not be null!", "labelNames"));

            final Histogram.Builder histogramBuilder = Histogram.build()
                    .name(name + "_latency_seconds_histogram")
                    .help(help)
                    .labelNames(labelNames);
            if (buckets != null) {
                histogramBuilder.buckets(buckets);
            }
            final Histogram histogram = histogramBuilder
                    //.namespace("namespace")
                    //.subsystem("subsystem")
                    //.buckets(0.0001, 0.001, 0.005, 0.010, 0.020, 0.050, 0.1, 1)
                    .register();

            final Gauge concurrent = Gauge.build()
                    .name(name + "_concurrent_gauge")
                    .help(help)
                    .labelNames(labelNames)
                    .register();

            final Gauge max = Gauge.build()
                    .name(name + "_max_gauge")
                    .help(help)
                    .labelNames(labelNames)
                    .register();

            final Gauge min = Gauge.build()
                    .name(name + "_min_gauge")
                    .help(help)
                    .labelNames(labelNames)
                    .register();

            final Counter errCounter = Counter.build()
                    .name(name + "_err_counter")
                    .help(help)
                    .labelNames(labelNames)
                    .register();

            final LatencyStat latencyStat = new LatencyStat(histogram, min, max, concurrent, errCounter, tag);
            LatencyProfiler.PROFILER_STAT_SET.add(latencyStat);
            return latencyStat;
        }

    }
}
