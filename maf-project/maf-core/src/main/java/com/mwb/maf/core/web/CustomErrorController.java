package com.mwb.maf.core.web;

import com.alibaba.fastjson.JSON;
import io.prometheus.client.Counter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Deprecated
//@Controller
public class CustomErrorController implements ErrorController {
    private final Counter errCounter = Counter.build()
            .name("app_http_incoming_err_code_counter")
            .help("app_http_incoming_err_code_counter")
            .labelNames("url", "status")
            .register();

    @RequestMapping("/error")
    @ResponseBody
    public String handleError(HttpServletRequest request) {
        Integer status = (Integer) request.getAttribute("javax.servlet.error.status_code");
        String method = request.getMethod();
        String path = (String) request.getAttribute("javax.servlet.error.request_uri");
        if (StringUtils.isNotEmpty(path))
            path = method + " " + path;
        errCounter.labels(path, String.valueOf(status)).inc();
        Map<String, Object> errorAttributes = new LinkedHashMap<>();
        errorAttributes.put("timestamp", new Date());
        errorAttributes.put("status", status);
        errorAttributes.put("path", path);
        Exception exception = (Exception) request.getAttribute("javax.servlet.error.exception");

        String message = (String) request.getAttribute("javax.servlet.error.message");
        if (StringUtils.isEmpty(message)) {
            if (exception != null) {
                errorAttributes.put("message", exception.getMessage());
            } else {
                errorAttributes.put("message", "No message available");
            }
        } else {
            errorAttributes.put("message", message);
        }
        return JSON.toJSONString(errorAttributes);
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
