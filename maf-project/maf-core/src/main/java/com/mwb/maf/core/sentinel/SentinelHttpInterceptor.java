package com.mwb.maf.core.sentinel;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.mwb.maf.core.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class SentinelHttpInterceptor implements HandlerInterceptor {
    private static final String PREFIX = "http:in:";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws BlockException {
        Entry methodEntry, httpEntry;
        String url = HttpUtil.getPatternUrl(request.getRequestURI());
        String metrics = request.getMethod() + " " + url;
        try {
            httpEntry = SphU.entry("Http::In");
            methodEntry = SphU.entry(PREFIX + metrics);
            request.setAttribute("metrics", metrics);
            request.setAttribute("methodEntry", methodEntry);
            request.setAttribute("httpEntry", httpEntry);
        } catch (BlockException ex) {
            throw ex;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (ex != null) {
            Tracer.trace(ex);
        }
        Entry methodEntry = (Entry) request.getAttribute("methodEntry");
        Entry httpEntry = (Entry) request.getAttribute("httpEntry");
        if (methodEntry != null) {
            methodEntry.exit();
        }
        if (httpEntry != null) {
            httpEntry.exit();
        }
    }

}
