package com.mwb.maf.core.web;

import com.mwb.maf.core.util.HttpUtil;
import com.mwb.maf.core.util.TracingUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HttpTracingInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String traceId = request.getHeader(TracingUtils.LABEL_TRACE_ID);
        if (StringUtils.isEmpty(traceId)) {
            traceId = System.currentTimeMillis() + "";
        }
        String source = HttpUtil.getClientRealIp(request);
        ThreadContext.put(TracingUtils.LABEL_TRACE_ID, traceId);
        ThreadContext.put(TracingUtils.LABEL_TRACE_SOURCE, source);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        ThreadContext.clearAll();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

    }
}
