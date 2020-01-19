package com.mwb.app.sample.db.impl;

import com.mwb.rpc.api.ShopApi;
import com.weibo.api.motan.config.springsupport.annotation.MotanService;
import lombok.extern.slf4j.Slf4j;

/**
 * 描述:
 *
 * @author mengweibo@kanzhun.com
 * @create 2020/1/17
 */

@Slf4j
@MotanService(basicService = "shopBasicServiceConfigBean")
public class ShopApiImpl implements ShopApi {

    @Override
    public String checkShopStatus(int id, String name) {
        log.info("checkShopStatus id={}, name={}", id, name);
        return id + name;
    }
}
