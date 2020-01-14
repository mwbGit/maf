package com.mwb.maf.core.web;

import com.google.common.collect.Maps;
import com.mwb.maf.core.base.ApiResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/maf")
public class ProjectInfoController {

    @RequestMapping("/info")
    @ResponseBody
    public ApiResult getPayConfig() {
        Map<String, String> map = Maps.newHashMap();
        map.put("availableProcessors", String.valueOf(Runtime.getRuntime().availableProcessors()));
        return ApiResult.success(map);
    }
}
