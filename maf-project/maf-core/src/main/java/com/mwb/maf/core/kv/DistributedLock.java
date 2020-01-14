package com.mwb.maf.core.kv;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;


public class DistributedLock {
    private static final Long RELEASE_SUCCESS = 1L;
    private static final String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

    public static Lock tryLock(RedisCommand client, String lockKey, int ttlMilliseconds) {
        String ticket = System.nanoTime() + "";
        String result = client.set(lockKey, ticket, "NX", "PX", ttlMilliseconds);
        if ("OK".equals(result)) {
            return new Lock(ticket, ticket);
        }
        return new Lock(null, lockKey);
    }

    public static boolean releaseLock(RedisScriptingClusterCommands client, Lock lock) {
        Object result = client.eval(script, Collections.singletonList(lock.getLockKey()), Collections.singletonList(lock.getTicket()));
        if (RELEASE_SUCCESS.equals(result)) {
            return true;
        }
        return false;
    }

    public static boolean releaseLock(RedisScriptingCommands client, Lock lock) {
        Object result = client.eval(script, Collections.singletonList(lock.getLockKey()), Collections.singletonList(lock.getTicket()));
        if (RELEASE_SUCCESS.equals(result)) {
            return true;
        }
        return false;
    }

    @Getter
    @AllArgsConstructor
    public static class Lock {
        private String ticket;
        private String lockKey;

        public boolean success() {
            return StringUtils.isNotEmpty(ticket);
        }
    }


}
