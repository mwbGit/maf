package com.mwb.maf.core.rpc;

import com.google.common.collect.Sets;
import com.mwb.maf.core.metrics.LatencyProfiler;
import com.mwb.maf.core.metrics.LatencyStat;
import com.mwb.maf.core.metrics.MonitorConfig;
import com.mwb.maf.core.util.MotanUtils;
import com.weibo.api.motan.core.extension.Activation;
import com.weibo.api.motan.core.extension.SpiMeta;
import com.weibo.api.motan.filter.Filter;
import com.weibo.api.motan.rpc.Caller;
import com.weibo.api.motan.rpc.Provider;
import com.weibo.api.motan.rpc.Request;
import com.weibo.api.motan.rpc.Response;
import io.prometheus.client.Gauge;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@SpiMeta(name = "cafProfiler")
@Activation(sequence = 2)
public class MotanProfilerFilter implements Filter {
    private static final LatencyStat PROFILER_STAT_IN = LatencyProfiler.Builder.build()
            .name("app_motan_requests_in")
            .defineLabels("method", "service")
            .tag("motan:incoming")
            .create();
    private static final LatencyStat PROFILER_STAT_OUT = LatencyProfiler.Builder.build()
            .name("app_motan_requests_out")
            .defineLabels("method", "service")
            .tag("motan:outgoing")
            .create();
    private static final Gauge FTL_IN_STAT = Gauge.build()
            .name("app_motan_requests_in_ftl")
            .help("app_motan_requests_in_ftl")
            .labelNames("method", "service")
            .register();
    private static final Gauge FTL_OUT_STAT = Gauge.build()
            .name("app_motan_requests_out_ftl")
            .help("app_motan_requests_out_ftl")
            .labelNames("method", "service")
            .register();

    private static final Set<String> FTL_IN_SET = Sets.newConcurrentHashSet();
    private static final Set<String> FTL_OUT_SET = Sets.newConcurrentHashSet();

    @Override
    public Response filter(Caller<?> caller, Request request) {
        if (!MonitorConfig.DYNAMIC_ENABLE_MOTAN_PROFILE) {
            return caller.call(request);
        }

        final String category = MotanUtils.getShortName(request.getInterfaceName());
        final String metrics = category + "." + request.getMethodName() + "(" + MotanUtils.getShortName(request.getParamtersDesc()) + ")";

        if (StringUtils.isEmpty(metrics) || StringUtils.isEmpty(category)) {
            return caller.call(request);
        }
        long begin = System.nanoTime();
        boolean specialException = true;
        boolean isError = false;
        final boolean ftlFlag = beforeCall(metrics, category, caller instanceof Provider);
        try {
            final Response response = caller.call(request);
            if (response == null) {
                isError = true;
            } else {
                if (response.getException() != null) {
                    isError = true;
                }
            }
            specialException = false;
            return response;
        } finally {
            if (specialException) {
                isError = true;
            }
            postCall(metrics, category, caller instanceof Provider, begin, isError, ftlFlag);
        }
    }

    private void postCall(String metrics, String category, boolean isIncoming, long begin, boolean isError, boolean ftlFlag) {
        if (isIncoming) {
            PROFILER_STAT_IN.observe(metrics, category, System.nanoTime() - begin, TimeUnit.NANOSECONDS);
            PROFILER_STAT_IN.dec(metrics, category);
            if (isError) {
                PROFILER_STAT_IN.error(metrics, category);
            }
            final String key = metrics + "::" + category;
            if (ftlFlag) {
                FTL_IN_STAT.labels(metrics, category).set(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - begin));
            }
        } else {
            PROFILER_STAT_OUT.observe(metrics, category, System.nanoTime() - begin, TimeUnit.NANOSECONDS);
            PROFILER_STAT_OUT.dec(metrics, category);
            if (isError) {
                PROFILER_STAT_OUT.error(metrics, category);
            }
            final String key = metrics + "::" + category;
            if (ftlFlag) {
                FTL_OUT_STAT.labels(metrics, category).set(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - begin));
            }
        }
    }

    private boolean beforeCall(String metrics, String category, boolean isIncoming) {
        boolean ftlFlag = false;
        String key = metrics + "::" + category;
        if (isIncoming) {
            if (!FTL_IN_SET.contains(key)) {
                FTL_IN_SET.add(key);
                ftlFlag = true;
            }
            PROFILER_STAT_IN.inc(metrics, category);
        } else {
            if (!FTL_OUT_SET.contains(key)) {
                FTL_OUT_SET.add(key);
                ftlFlag = true;
            }
            PROFILER_STAT_OUT.inc(metrics, category);
        }
        return ftlFlag;
    }
}
