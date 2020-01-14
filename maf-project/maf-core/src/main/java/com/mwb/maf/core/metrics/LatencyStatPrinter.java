package com.mwb.maf.core.metrics;

import com.google.common.collect.Maps;
import com.mwb.maf.core.logging.Loggers;
import com.mwb.maf.core.util.LogUtils;
import com.mwb.maf.core.util.NumberUtil;
import io.prometheus.client.Collector;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LatencyStatPrinter implements Runnable {
    private static final Logger log = Loggers.getPerformanceLogger();
    private static final String SPLIT = "| ";
    private static final Map<String, Double> SNAPSHOT_LAST_STAT = Maps.newConcurrentMap();

    private static final int LABEL_SIZE_METRICS = 75;
    private static final int LABEL_SIZE_MIN = 10;
    private static final int LABEL_SIZE_MAX = 10;
    private static final int LABEL_SIZE_AVG = 10;
    private static final int LABEL_SIZE_CONCURRENT = 11;
    private static final int LABEL_SIZE_COUNT = 16;
    private static final int LABEL_SIZE_QPS = 8;
    private static final int LABEL_SIZE = LABEL_SIZE_METRICS +
            LABEL_SIZE_MIN +
            LABEL_SIZE_MAX +
            LABEL_SIZE_AVG +
            LABEL_SIZE_CONCURRENT +
            LABEL_SIZE_COUNT +
            LABEL_SIZE_QPS +
            SPLIT.length() * 2;

    private MonitorConfig monitorConfig = new MonitorConfig();

    public LatencyStatPrinter(MonitorConfig monitorConfig) {
        this.monitorConfig = monitorConfig;
    }

    @Override
    public void run() {
        for (LatencyStat latencyStat : LatencyProfiler.PROFILER_STAT_SET) {
            LogUtils.putContextColumn1("perf");
            LogUtils.putContextColumn2(latencyStat.getTag() + ":" + DateTime.now().toString("yyyyMMddHHmmss"));
            try {
                logStats(latencyStat);
            } catch (Throwable t) {
                log.error("", t);
            } finally {
                LogUtils.clearContext();
            }
        }
    }

    private void logStats(LatencyStat latencyStat) {
        log.info(LogUtils.line(LABEL_SIZE));
        String sb = SPLIT +
                StringUtils.rightPad("Metrics", LABEL_SIZE_METRICS) +
                StringUtils.leftPad("Min(ms)", LABEL_SIZE_MIN) +
                StringUtils.leftPad("Max(ms)", LABEL_SIZE_MAX) +
                StringUtils.leftPad("Avg(ms)", LABEL_SIZE_AVG) +
                StringUtils.leftPad("Concurrent", LABEL_SIZE_CONCURRENT) +
                StringUtils.leftPad("Count(err/sum)", LABEL_SIZE_COUNT) +
                StringUtils.leftPad("Qps", LABEL_SIZE_QPS) +
                " " + SPLIT;
        log.info(sb);
        List<Collector.MetricFamilySamples> familySamples = latencyStat.getHistogram().collect();
        for (Collector.MetricFamilySamples samples : familySamples) {
            if (CollectionUtils.isEmpty(samples.samples)) {
                //log.info(StringUtils.right(SPLIT + "N/A" + " " + SPLIT, LABEL_SIZE));
                continue;
            }
            //rocketmq-prometheus的指标大于2个，需要过滤，否则后边打印报错。
            if (samples.name != null && samples.name.startsWith("rocketmq_")) {
                continue;
            }
            Map<String, List<Collector.MetricFamilySamples.Sample>> collect = samples.samples.stream().collect(Collectors.groupingBy(v -> v.labelValues.get(1)));
            boolean multiNamespace = MapUtils.size(collect) > 1 && StringUtils.equals("namespace", samples.samples.get(0).labelNames.get(1));
            if (MapUtils.isNotEmpty(collect)) {
                collect.forEach((k, v) -> {
                    double dCount = 0D;
                    double dSum = 0D;
                    String url = null;
                    String endpoint = null;
                    final Map<String, List<Collector.MetricFamilySamples.Sample>> listMap = v.stream().collect(Collectors.groupingBy(vv -> vv.labelValues.get(0)));
                    if (MapUtils.isNotEmpty(listMap)) {
                        for (Map.Entry<String, List<Collector.MetricFamilySamples.Sample>> entry : listMap.entrySet()) {
                            for (Collector.MetricFamilySamples.Sample sample : entry.getValue()) {
                                if (StringUtils.endsWith(sample.name, "_count")) {
                                    dCount = NumberUtil.getDouble(sample.value);
                                    continue;
                                }
                                if (StringUtils.endsWith(sample.name, "_sum")) {
                                    dSum = NumberUtil.getDouble(sample.value);
                                    continue;
                                }
                                url = sample.labelValues.get(0);
                                endpoint = sample.labelValues.get(1);
                            }
                            double dConcurrent = latencyStat.getConcurrent().labels(url, endpoint) == null ? 0d : latencyStat.getConcurrent().labels(url, endpoint).get();
                            double errCount = latencyStat.getError().labels(url, endpoint) == null ? 0d : latencyStat.getError().labels(url, endpoint).get();
                            double min = latencyStat.getMin().labels(url, endpoint) == null ? 0d : latencyStat.getMin().labels(url, endpoint).get();
                            double max = latencyStat.getMax().labels(url, endpoint) == null ? 0d : latencyStat.getMax().labels(url, endpoint).get();
                            final String sKeyPrefix = latencyStat.getTag() + ":" + "interval" + ":" + endpoint + ":" + url;
                            final Double incErrCount = getIncValueAndSetNew(sKeyPrefix + ":err-count", errCount);
                            final Double incCount = getIncValueAndSetNew(sKeyPrefix + ":count", dCount);
                            final Double incSum = getIncValueAndSetNew(sKeyPrefix + ":sum", dSum);
                            String sba = SPLIT +
                                    StringUtils.rightPad(multiNamespace ? endpoint + "::" + url : url, LABEL_SIZE_METRICS) +
                                    StringUtils.leftPad(String.format("%.1f", min * 1000), LABEL_SIZE_MIN) +
                                    StringUtils.leftPad(String.format("%.1f", max * 1000), LABEL_SIZE_MAX) +
                                    StringUtils.leftPad(String.format("%.1f", (incCount != 0D) ? incSum / incCount * 1000 : 0D), LABEL_SIZE_AVG) + //Avg(ms)
                                    StringUtils.leftPad(String.format("%.0f", dConcurrent), LABEL_SIZE_CONCURRENT) +
                                    StringUtils.leftPad(String.format("%.0f/%.0f", incErrCount, incCount), LABEL_SIZE_COUNT) + //Count(err/sum)
                                    StringUtils.leftPad(String.format("%.1f", incCount / monitorConfig.getLogPeriod()), LABEL_SIZE_QPS) + //Qps
                                    " " + SPLIT;
                            log.info(sba);
                        }
                    }
                });
            }
            latencyStat.reset();
        }
        log.info(LogUtils.line(LABEL_SIZE));
    }

    private Double getIncValueAndSetNew(String key, double newValue) {
        final Double result = newValue - SNAPSHOT_LAST_STAT.getOrDefault(key, 0D);
        SNAPSHOT_LAST_STAT.put(key, newValue);
        return result;
    }
}
