package com.mwb.app.sample.db.controller;

import com.mwb.app.sample.db.mapper.ShopMapper;
import com.mwb.app.sample.db.model.Shop;
import com.mwb.maf.core.base.ApiResult;
import com.mwb.maf.core.kv.JedisClient;
import com.mwb.maf.core.kv.JedisClusterClient;
import com.mwb.rpc.api.UserApi;
import com.weibo.api.motan.config.springsupport.annotation.MotanReferer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 描述:
 *
 * @author mengweibo@kanzhun.com
 * @create 2020/1/13
 */
@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    @MotanReferer(basicReferer = "userBasicRefererConfigBean")
    private UserApi userApi;

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    @Qualifier("mwbJedisClusterClient")
    private JedisClusterClient jedisClusterClient;

    @Autowired
    private JedisClient jedisClient;

    @Autowired
    private ApplicationContext applicationContext;

    @RequestMapping("/redis")
    public ApiResult redis() {
        log.info("======redis=========");
        String ok = jedisClient.set("aaa", "test redis");
        log.info("======redis=========" + ok);

        return ApiResult.success(jedisClient.get("aaa"));
    }

    @RequestMapping("/redisCluster")
    public ApiResult redisCluster() {
        log.info("======redisCluster=========");
        String ok = jedisClusterClient.set("redisCluster", "test redisCluster");
        log.info("======redisCluster=========" + ok);
        return ApiResult.success(jedisClusterClient.get("redisCluster"));
    }


    @RequestMapping("/db")
    public ApiResult db() {
        log.info("======db=========");
        Shop shop = shopMapper.findById(1);
        return ApiResult.success(shop);
    }

    @RequestMapping("/rpc")
    public ApiResult rpc() {
        log.info("======rpc=========");
        boolean status = userApi.checkStatus(111);
        return ApiResult.success(status);
    }
}
