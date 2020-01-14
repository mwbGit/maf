package com.mwb.maf.core.kv;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractRedisClient implements RedisCommand {

    public boolean setCache(String key, Object obj) {
        return StringUtils.equals("OK", set(key, JSON.toJSONString(obj)));
    }

    public boolean setCache(String key, Object obj, int timeout) {
        return StringUtils.equals("OK", setex(key, timeout, JSON.toJSONString(obj)));
    }

    public <T> T getCache(String key, TypeReference<T> typeReference) {
        String value = get(key);
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        return JSON.parseObject(value, typeReference);
    }
}
