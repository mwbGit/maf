package com.mwb.maf.core.web;

import com.mwb.maf.core.metrics.LatencyProfiler;
import com.mwb.maf.core.metrics.LatencyStat;
import com.mwb.maf.core.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Deprecated
public class HttpProfilerInterceptor implements HandlerInterceptor {
    private static final LatencyStat PROFILER_STAT = LatencyProfiler.Builder.build()
            .name("app_http_incoming_requests")
            .defineLabels("url", "endpoint")
//            .buckets(
//                    0.0001, 0.0005, 0.0009,
//                    0.001, 0.003, 0.005, 0.007, 0.009,
//                    0.01, 0.03, 0.05, 0.07, 0.09,
//                    0.1, 0.3, 0.5, 0.7, 0.9,
//                    1, 3, 5)
            .tag("http:incoming")
            .create();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            String url = HttpUtil.getPatternUrl(request.getRequestURI());
            if (StringUtils.equals("/error/", url))
                return true;
            String metrics = request.getMethod() + " " + url;
            String category = HttpUtil.getUrlEndpoint(request.getRequestURI());
            if (StringUtils.isNotEmpty(metrics) && StringUtils.isNotEmpty(category)) {
                PROFILER_STAT.inc(metrics, category);
                request.setAttribute("metricsTimer", PROFILER_STAT.startTimer(metrics, category));
                request.setAttribute("metrics", metrics);
                request.setAttribute("category", category);
            }
        } catch (Throwable throwable) {
            log.error("", throwable);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String url = HttpUtil.getPatternUrl(request.getRequestURI());
        if (StringUtils.equals("/error/", url))
            return;
        String metrics = (String) request.getAttribute("metrics");
        String category = (String) request.getAttribute("category");
        LatencyStat.Timer metricsTimer = (LatencyStat.Timer) request.getAttribute("metricsTimer");
        if (metricsTimer != null) {
            metricsTimer.observeDuration();
        }
        if (StringUtils.isNotEmpty(metrics) && StringUtils.isNotEmpty(category)) {
            PROFILER_STAT.dec(metrics, category);
        }
        if (ex != null) {
            PROFILER_STAT.error(metrics, category);
        }
    }

}
