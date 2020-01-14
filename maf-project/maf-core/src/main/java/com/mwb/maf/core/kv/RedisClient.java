package com.mwb.maf.core.kv;

public abstract class RedisClient<T> extends AbstractRedisClient implements RedisMultiKeyCommands, RedisScriptingCommands {
    public abstract T getDriver();

}
