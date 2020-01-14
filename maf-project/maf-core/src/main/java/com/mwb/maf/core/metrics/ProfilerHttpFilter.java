package com.mwb.maf.core.metrics;

import com.google.common.collect.Sets;
import com.mwb.maf.core.util.HttpUtil;
import io.prometheus.client.Gauge;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ProfilerHttpFilter implements Filter {
    private static final LatencyStat PROFILER_STAT = LatencyProfiler.Builder.build()
            .name("app_http_incoming_requests")
            .defineLabels("url", "status")
//            .buckets(
//                    0.0001, 0.0005, 0.0009,
//                    0.001, 0.003, 0.005, 0.007, 0.009,
//                    0.01, 0.03, 0.05, 0.07, 0.09,
//                    0.1, 0.3, 0.5, 0.7, 0.9,
//                    1, 3, 5)
            .tag("http:incoming")
            .create();
    private static final Gauge FTL_STAT = Gauge.build()
            .name("app_http_incoming_requests_ftl_gauge")
            .help("app_http_incoming_requests_ftl_gauge")
            .labelNames("url", "status")
            .register();
    private static final Set<String> FTL_URL_SET = Sets.newConcurrentHashSet();

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest sRequest = (HttpServletRequest) request;
        HttpServletResponse sResponse = (HttpServletResponse) response;
        String url = HttpUtil.getPatternUrl(sRequest.getRequestURI());
        String metrics = sRequest.getMethod() + " " + url;
        long begin = System.currentTimeMillis();
        PROFILER_STAT.inc(metrics, "");
        boolean ftlFlag = false;
        if (!FTL_URL_SET.contains(metrics)) {
            FTL_URL_SET.add(metrics);
            ftlFlag = true;
        }
        try {
            chain.doFilter(request, response);
        } catch (IOException e) {
            PROFILER_STAT.error(metrics, IOException.class.getSimpleName());
            throw e;
        } catch (ServletException e) {
            PROFILER_STAT.error(metrics, ServletException.class.getSimpleName());
            throw e;
        } finally {
            PROFILER_STAT.dec(metrics, "");
            PROFILER_STAT.observe(metrics, String.valueOf(sResponse.getStatus()), System.currentTimeMillis() - begin, TimeUnit.MILLISECONDS);
            if (ftlFlag) {
                FTL_STAT.labels(metrics, String.valueOf(sResponse.getStatus())).set(System.currentTimeMillis() - begin);
            }
        }
    }

    @Override
    public void destroy() {

    }

}