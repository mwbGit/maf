package com.mwb.rpc.impl;

import com.mwb.rpc.api.UserApi;
import com.weibo.api.motan.config.springsupport.annotation.MotanService;
import lombok.extern.slf4j.Slf4j;

/**
 * 描述:
 *
 * @author mengweibo@kanzhun.com
 * @create 2020/1/14
 */
@Slf4j
@MotanService(basicService = "userBasicServiceConfigBean")
public class UserApiImpl implements UserApi {

    @Override
    public boolean checkStatus(int params) {
        log.info("checkStatus params={}", params);
        return true;
    }
}
