package com.mwb.maf.core.web;

import com.alibaba.fastjson.JSON;
import com.mwb.maf.core.logging.Loggers;
import com.mwb.maf.core.logging.LoggingNoticeEvent;
import com.mwb.maf.core.logging.MafWebLog;
import com.mwb.maf.core.util.HttpRequestUtil;
import com.mwb.maf.core.util.TracingUtils;
import com.weibo.api.motan.util.NetUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class HttpTracingInterceptor implements HandlerInterceptor, ApplicationContextAware {
    private static final Logger logger = Loggers.getTraceLogger();

    private ApplicationContext applicationContext;

    public HttpTracingInterceptor(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String traceId = request.getHeader(TracingUtils.LABEL_TRACE_ID);
        if (StringUtils.isEmpty(traceId)) {
            traceId = TracingUtils.createTraceId();
        }
        TracingUtils.putTraceId(traceId);
        MafWebLog mafWebLog = null;
        try {
            mafWebLog = new MafWebLog(traceId, NetUtils.getLocalAddress().getHostAddress(), JSON.toJSONString(HttpRequestUtil.getPathParams(request)),
                    HttpRequestUtil.getUri(request), request.getMethod(), HttpRequestUtil.getRequestHeaders(request), HttpRequestUtil.getRequestBody(request), HttpRequestUtil.getIp(request));
            mafWebLog.setRequestIp(HttpRequestUtil.getIp(request));
        } catch (Exception e) {
            mafWebLog = new MafWebLog();
            mafWebLog.setTraceId(traceId);
        } finally {
            TracingUtils.putTraceLog(mafWebLog);
        }
        logger.info("http start traceId = {}, info = {}", traceId, mafWebLog);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        MafWebLog mafWebLog = TracingUtils.geWebTraceLog();
        if (mafWebLog != null) {
            try {
                mafWebLog.setResponse(response.getStatus(), HttpRequestUtil.getResponseHeaders(response), HttpRequestUtil.getResponseBody(response));
                applicationContext.publishEvent(new LoggingNoticeEvent(this, mafWebLog));
                logger.info("http end traceId = {}, info = {}", mafWebLog.getTraceId(), mafWebLog);
            } catch (Exception e) {
                log.error("http postHandle err logJson={}", mafWebLog, e);
            }
        }
        TracingUtils.cleanAll();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
