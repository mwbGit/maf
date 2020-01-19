package com.mwb.maf.core.util;

import com.mwb.maf.core.web.RequestWrapper;
import com.mwb.maf.core.web.ResponseWrapper;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 描述:
 *
 * @author mengweibo@kanzhun.com
 * @create 2020/1/16
 */
public class HttpRequestUtil {
    public static String getIp(HttpServletRequest request) {
        Assert.notNull(request, "request instance is null.");
        String Xip = request.getHeader("X-Real-IP");
        String XFor = request.getHeader("X-Forwarded-For");
        if (!StringUtils.isEmpty(XFor) && !"unKnown".equalsIgnoreCase(XFor)) {
            int index = XFor.indexOf(",");
            return index != -1 ? XFor.substring(0, index) : XFor;
        } else {
            XFor = Xip;
            if (!StringUtils.isEmpty(Xip) && !"unKnown".equalsIgnoreCase(Xip)) {
                return Xip;
            } else {
                if (StringUtils.isEmpty(Xip) || "unknown".equalsIgnoreCase(Xip)) {
                    XFor = request.getHeader("Proxy-Client-IP");
                }

                if (StringUtils.isEmpty(XFor) || "unknown".equalsIgnoreCase(XFor)) {
                    XFor = request.getHeader("WL-Proxy-Client-IP");
                }

                if (StringUtils.isEmpty(XFor) || "unknown".equalsIgnoreCase(XFor)) {
                    XFor = request.getHeader("HTTP_CLIENT_IP");
                }

                if (StringUtils.isEmpty(XFor) || "unknown".equalsIgnoreCase(XFor)) {
                    XFor = request.getHeader("HTTP_X_FORWARDED_FOR");
                }

                if (StringUtils.isEmpty(XFor) || "unknown".equalsIgnoreCase(XFor)) {
                    XFor = request.getRemoteAddr();
                }

                return XFor;
            }
        }
    }

    public static Map<String, String> getRequestHeaders(HttpServletRequest request) {
        Assert.notNull(request, "request instance is null.");
        Map<String, String> headers = new HashMap();
        Enumeration enumeration = request.getHeaderNames();

        while (enumeration.hasMoreElements()) {
            String headerName = (String) enumeration.nextElement();
            String headerValue = request.getHeader(headerName);
            headers.put(headerName, headerValue);
        }

        return headers;
    }

    public static Map<String, String> getResponseHeaders(HttpServletResponse response) {
        Assert.notNull(response, "response instance is null.");
        Map<String, String> headers = new HashMap();
        Iterator iterator = response.getHeaderNames().iterator();

        while (iterator.hasNext()) {
            String headerName = (String) iterator.next();
            String headerValue = response.getHeader(headerName);
            headers.put(headerName, headerValue);
        }

        return headers;
    }

    public static String getHeader(HttpServletRequest request, String headerName) {
        Assert.notNull(request, "request instance is null.");
        Assert.notNull(headerName, "request header name is null.");
        return request.getHeader(headerName);
    }

    public static Map getPathParams(HttpServletRequest request) {
        Assert.notNull(request, "request instance is null.");
        Map map = new HashMap();
        Enumeration paramNames = request.getParameterNames();

        while (paramNames.hasMoreElements()) {
            String paramName = (String) paramNames.nextElement();
            String[] paramValues = request.getParameterValues(paramName);
            if (paramValues.length == 1) {
                String paramValue = paramValues[0];
                if (paramValue.length() != 0) {
                    map.put(paramName, paramValue);
                }
            }
        }

        return map;
    }

    public static String getUri(HttpServletRequest request) {
        Assert.notNull(request, "request instance is null.");
        return request.getRequestURI();
    }

    public static String getResponseBody(HttpServletResponse response) {
        if (response instanceof ResponseWrapper) {
            ResponseWrapper responseWrapper = (ResponseWrapper) response;
            byte[] copy = responseWrapper.getCopy();
            try {
                return new String(copy, "UTF-8");
            } catch (UnsupportedEncodingException e) {
            }
        }
        return null;
    }

    public static String getRequestBody(HttpServletRequest request) {
        Assert.notNull(request, "request instance is null.");
        RequestWrapper requestWrapper;
        if (request instanceof RequestWrapper) {
            requestWrapper = (RequestWrapper) request;
        } else {
            requestWrapper = new RequestWrapper(request);
        }

        return requestWrapper.getBody();
    }
}
