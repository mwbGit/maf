package com.mwb.maf.core.kv;

public abstract class RedisClusterClient<T> extends AbstractRedisClient implements RedisMultiKeyClusterCommands, RedisScriptingClusterCommands {
    public abstract T getDriver();
}
