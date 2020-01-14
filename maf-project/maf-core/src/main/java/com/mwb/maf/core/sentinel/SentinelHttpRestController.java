package com.mwb.maf.core.sentinel;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.mwb.maf.core.base.ApiResult;
import com.mwb.maf.core.logging.Loggers;
import com.mwb.maf.core.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@ControllerAdvice
public class SentinelHttpRestController {

    @ExceptionHandler({BlockException.class})
    @ResponseBody
    public ApiResult handle(HttpServletRequest request, HttpServletResponse response, BlockException ex) {
//    public WebResult handle(HttpServletRequest request, HttpServletResponse response) {
        String metrics = request.getMethod() + " " + HttpUtil.getPatternUrl(request.getRequestURI());
        Loggers.getAccessLogger().warn("http request blocked! -> {}", metrics);
        return ApiResult.failed("block...");
    }

}