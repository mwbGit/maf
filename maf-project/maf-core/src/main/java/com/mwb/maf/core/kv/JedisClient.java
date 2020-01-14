package com.mwb.maf.core.kv;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Tuple;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JedisClient extends RedisClient<Jedis> {
    private static final Long RELEASE_SUCCESS = 1L;
    private String namespace;
    private CJedisPool jedisPool;
    private JedisPoolConfig jedisPoolConfig;
    private String address;
    private int port;

    public JedisPool getJedisPool() {
        return jedisPool;
    }

    public JedisPoolConfig getJedisPoolConfig() {
        return jedisPoolConfig;
    }

    public JedisClient(CJedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public JedisClient(String namespace, JedisPoolConfig jedisPoolConfig, String address, int port) {
        this.namespace = namespace;
        this.jedisPoolConfig = jedisPoolConfig;
        this.address = address;
        this.port = port;
        this.jedisPool = new CJedisPool(jedisPoolConfig, address, port);
    }

    public void warmUp() {
        jedisPool.warmUp();
    }

    public boolean tryLock(String lockKey, String requestId, int expireTime) {
        try (Jedis jedis = jedisPool.getResource()) {
            String result = jedis.set(lockKey, requestId, "NX", "PX", expireTime);
            if ("OK".equals(result)) {
                return true;
            }
        }
        return false;

    }

    public boolean releaseDistributedLock(String lockKey, String requestId) {
        try (Jedis jedis = jedisPool.getResource()) {
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));
            if (RELEASE_SUCCESS.equals(result)) {
                return true;
            }
        }
        return false;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    @Override
    public Jedis getDriver() {
        return jedisPool.getResource();
    }

    @Override
    public String set(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.set(key, value);
        }
    }

    @Override
    public String set(String key, String value, String nxxx, String expx, long time) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.set(key, value, nxxx, expx, time);
        }
    }

    @Override
    public String set(String key, String value, String nxxx) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.set(key, value, nxxx);
        }
    }

    @Override
    public String get(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        }
    }

    @Override
    public Boolean exists(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.exists(key);
        }
    }

    @Override
    public Long persist(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.persist(key);
        }
    }

    @Override
    public String type(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.type(key);
        }
    }

    @Override
    public Long expire(String key, int seconds) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.expire(key, seconds);
        }
    }

    @Override
    public Long pexpire(String key, long milliseconds) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.pexpire(key, milliseconds);
        }
    }

    @Override
    public Long expireAt(String key, long unixTime) {
        try (Jedis jedis = jedisPool.getResource()) {
            return expireAt(key, unixTime);
        }
    }

    @Override
    public Long pexpireAt(String key, long millisecondsTimestamp) {
        try (Jedis jedis = jedisPool.getResource()) {
            return pexpireAt(key, millisecondsTimestamp);
        }
    }

    @Override
    public Long ttl(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.ttl(key);
        }
    }

    @Override
    public Long pttl(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.pttl(key);
        }
    }

    @Override
    public Boolean setbit(String key, long offset, boolean value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.setbit(key, offset, value);
        }
    }

    @Override
    public Boolean setbit(String key, long offset, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.setbit(key, offset, value);
        }
    }

    @Override
    public Boolean getbit(String key, long offset) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.getbit(key, offset);
        }
    }

    @Override
    public Long setrange(String key, long offset, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.setrange(key, offset, value);
        }
    }

    @Override
    public String getrange(String key, long startOffset, long endOffset) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.getrange(key, startOffset, endOffset);
        }
    }

    @Override
    public String getSet(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.getSet(key, value);
        }
    }

    @Override
    public Long setnx(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.setnx(key, value);
        }
    }

    @Override
    public String setex(String key, int seconds, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.setex(key, seconds, value);
        }
    }

    @Override
    public String psetex(String key, long milliseconds, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.psetex(key, milliseconds, value);
        }
    }

    @Override
    public Long decrBy(String key, long integer) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.decrBy(key, integer);
        }
    }

    @Override
    public Long decr(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.decr(key);
        }
    }

    @Override
    public Long incrBy(String key, long integer) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.incrBy(key, integer);
        }
    }

    @Override
    public Double incrByFloat(String key, double value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.incrByFloat(key, value);
        }
    }

    @Override
    public Long incr(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.incr(key);
        }
    }

    @Override
    public Long append(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.append(key, value);
        }
    }

    @Override
    public String substr(String key, int start, int end) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.substr(key, start, end);
        }
    }

    @Override
    public Long hset(String key, String field, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hset(key, field, value);
        }
    }

    @Override
    public String hget(String key, String field) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hget(key, field);
        }
    }

    @Override
    public Long hsetnx(String key, String field, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hsetnx(key, field, value);
        }
    }

    @Override
    public String hmset(String key, Map<String, String> hash) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hmset(key, hash);
        }
    }

    @Override
    public List<String> hmget(String key, String... fields) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hmget(key, fields);
        }
    }

    @Override
    public Long hincrBy(String key, String field, long value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hincrBy(key, field, value);
        }
    }

    @Override
    public Double hincrByFloat(String key, String field, double value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hincrByFloat(key, field, value);
        }
    }

    @Override
    public Boolean hexists(String key, String field) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hexists(key, field);
        }
    }

    @Override
    public Long hdel(String key, String... field) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hdel(key, field);
        }
    }

    @Override
    public Long hlen(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hlen(key);
        }
    }

    @Override
    public Set<String> hkeys(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hkeys(key);
        }
    }

    @Override
    public List<String> hvals(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hvals(key);
        }
    }

    @Override
    public Map<String, String> hgetAll(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hgetAll(key);
        }
    }

    @Override
    public Long rpush(String key, String... string) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.rpush(key, string);
        }
    }

    @Override
    public Long lpush(String key, String... string) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lpush(key, string);
        }
    }

    @Override
    public Long llen(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.llen(key);
        }
    }

    @Override
    public List<String> lrange(String key, long start, long end) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lrange(key, start, end);
        }
    }

    @Override
    public String ltrim(String key, long start, long end) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.ltrim(key, start, end);
        }
    }

    @Override
    public String lindex(String key, long index) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lindex(key, index);
        }
    }

    @Override
    public String lset(String key, long index, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lset(key, index, value);
        }
    }

    @Override
    public Long lrem(String key, long count, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lrem(key, count, value);
        }
    }

    @Override
    public String lpop(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lpop(key);
        }
    }

    @Override
    public String rpop(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.rpop(key);
        }
    }

    @Override
    public Long sadd(String key, String... member) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.sadd(key, member);
        }
    }

    @Override
    public Set<String> smembers(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.smembers(key);
        }
    }

    @Override
    public Long srem(String key, String... member) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.srem(key, member);
        }
    }

    @Override
    public String spop(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.spop(key);
        }
    }

    @Override
    public Set<String> spop(String key, long count) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.spop(key, count);
        }
    }

    @Override
    public Long scard(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.scard(key);
        }
    }

    @Override
    public Boolean sismember(String key, String member) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.sismember(key, member);
        }
    }

    @Override
    public String srandmember(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.srandmember(key);
        }
    }

    @Override
    public List<String> srandmember(String key, int count) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.srandmember(key, count);
        }
    }

    @Override
    public Long strlen(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.strlen(key);
        }
    }

    @Override
    public Long zadd(String key, double score, String member) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zadd(key, score, member);
        }
    }

    @Override
    public Long zadd(String key, Map<String, Double> scoreMembers) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zadd(key, scoreMembers);
        }
    }

    @Override
    public Set<String> zrange(String key, long start, long end) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrange(key, start, end);
        }
    }

    @Override
    public Long zrem(String key, String... member) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrem(key, member);
        }
    }

    @Override
    public Double zincrby(String key, double score, String member) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zincrby(key, score, member);
        }
    }

    @Override
    public Long zrank(String key, String member) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrank(key, member);
        }
    }

    @Override
    public Long zrevrank(String key, String member) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrevrank(key, member);
        }
    }

    @Override
    public Set<String> zrevrange(String key, long start, long end) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrevrange(key, start, end);
        }
    }

    @Override
    public Long zcard(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zcard(key);
        }
    }

    @Override
    public Double zscore(String key, String member) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zscore(key, member);
        }
    }

    @Override
    public List<String> sort(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.sort(key);
        }
    }

    @Override
    public Long zcount(String key, double min, double max) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zcount(key, min, max);
        }
    }

    @Override
    public Long zcount(String key, String min, String max) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zcount(key, min, max);
        }
    }

    @Override
    public Set<String> zrangeByScore(String key, double min, double max) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrangeByScore(key, min, max);
        }
    }

    @Override
    public Set<String> zrangeByScore(String key, String min, String max) {
        try (Jedis jedis = jedisPool.getResource()) {
            return zrangeByScore(key, min, max);
        }
    }

    @Override
    public Set<String> zrevrangeByScore(String key, double max, double min) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrevrangeByScore(key, max, min);
        }
    }

    @Override
    public Set<String> zrangeByScore(String key, double min, double max, int offset, int count) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrangeByScore(key, min, max, offset, count);
        }
    }

    @Override
    public Set<String> zrevrangeByScore(String key, String max, String min) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrevrangeByScore(key, max, min);
        }
    }

    @Override
    public Set<String> zrangeByScore(String key, String min, String max, int offset, int count) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrangeByScore(key, min, max, offset, count);
        }
    }

    @Override
    public Set<String> zrevrangeByScore(String key, double max, double min, int offset, int count) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrevrangeByScore(key, max, min, offset, count);
        }
    }

    @Override
    public Set<String> zrevrangeByScore(String key, String max, String min, int offset, int count) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrevrangeByScore(key, max, min, offset, count);
        }
    }

    @Override
    public Set<Tuple> zrevrangeWithScores(String key, long start, long end) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrevrangeWithScores(key, start, end);
        }
    }

    @Override
    public Long zremrangeByRank(String key, long start, long end) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zremrangeByRank(key, start, end);
        }
    }

    @Override
    public Long zremrangeByScore(String key, double start, double end) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zremrangeByScore(key, start, end);
        }
    }

    @Override
    public Long zremrangeByScore(String key, String start, String end) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zremrangeByScore(key, start, end);
        }
    }

    @Override
    public Long zlexcount(String key, String min, String max) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zlexcount(key, min, max);
        }
    }

    @Override
    public Set<String> zrangeByLex(String key, String min, String max) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrangeByLex(key, min, max);
        }
    }

    @Override
    public Set<String> zrangeByLex(String key, String min, String max, int offset, int count) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrangeByLex(key, min, max, offset, count);
        }
    }

    @Override
    public Set<String> zrevrangeByLex(String key, String max, String min) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrevrangeByLex(key, max, min);
        }
    }

    @Override
    public Set<String> zrevrangeByLex(String key, String max, String min, int offset, int count) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrangeByLex(key, max, min, offset, count);
        }
    }

    @Override
    public Long zremrangeByLex(String key, String min, String max) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zremrangeByLex(key, min, max);
        }
    }

    @Override
    public Long lpushx(String key, String... string) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lpushx(key, string);
        }
    }

    @Override
    public Long rpushx(String key, String... string) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.rpushx(key, string);
        }
    }

    @Override
    public List<String> blpop(int timeout, String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.blpop(timeout, key);
        }
    }

    @Override
    public List<String> brpop(int timeout, String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.brpop(timeout, key);
        }
    }

    @Override
    public Long del(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.del(key);
        }
    }

    @Override
    public String echo(String string) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.echo(string);
        }
    }

    @Override
    public Long move(String key, int dbIndex) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.move(key, dbIndex);
        }
    }

    @Override
    public Long bitcount(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.bitcount(key);
        }
    }

    @Override
    public Long bitcount(String key, long start, long end) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.bitcount(key, start, end);
        }
    }

    @Override
    public Long bitpos(String key, boolean value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.bitpos(key, value);
        }
    }

    @Override
    public Long pfadd(String key, String... elements) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.pfadd(key, elements);
        }
    }

    @Override
    public long pfcount(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.pfcount(key);
        }
    }

    @Override
    public Long geoadd(String key, double longitude, double latitude, String member) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.geoadd(key, longitude, latitude, member);
        }
    }

    @Override
    public Double geodist(String key, String member1, String member2) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.geodist(key, member1, member2);
        }
    }

    @Override
    public List<String> geohash(String key, String... members) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.geohash(key, members);
        }
    }

    @Override
    public List<Long> bitfield(String key, String... arguments) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.bitfield(key, arguments);
        }
    }

    @Override
    public Long del(String... keys) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.del(keys);
        }
    }

    @Override
    public Long exists(String... keys) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.exists(keys);
        }
    }

    @Override
    public List<String> blpop(int timeout, String... keys) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.blpop(timeout, keys);
        }
    }

    @Override
    public List<String> brpop(int timeout, String... keys) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.brpop(timeout, keys);
        }
    }

    @Override
    public List<String> blpop(String... args) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.blpop(args);
        }
    }

    @Override
    public List<String> brpop(String... args) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.brpop(args);
        }
    }

    @Override
    public Set<String> keys(String pattern) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> mget(String... keys) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.mget(keys);
        }
    }

    @Override
    public String mset(String... keysvalues) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.mset(keysvalues);
        }
    }

    @Override
    public Long msetnx(String... keysvalues) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.msetnx(keysvalues);
        }
    }

    @Override
    public String rename(String oldkey, String newkey) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.rename(oldkey, newkey);
        }
    }

    @Override
    public Long renamenx(String oldkey, String newkey) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.renamenx(oldkey, newkey);
        }
    }

    @Override
    public String rpoplpush(String srckey, String dstkey) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.rpoplpush(srckey, dstkey);
        }
    }

    @Override
    public Set<String> sdiff(String... keys) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.sdiff(keys);
        }
    }

    @Override
    public Long sdiffstore(String dstkey, String... keys) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.sdiffstore(dstkey, keys);
        }
    }

    @Override
    public Set<String> sinter(String... keys) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.sinter(keys);
        }
    }

    @Override
    public Long sinterstore(String dstkey, String... keys) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.sinterstore(dstkey, keys);
        }
    }

    @Override
    public Long smove(String srckey, String dstkey, String member) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.smove(srckey, dstkey, member);
        }
    }

    @Override
    public Long sort(String key, String dstkey) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.sort(key, dstkey);
        }
    }

    @Override
    public Set<String> sunion(String... keys) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.sunion(keys);
        }
    }

    @Override
    public Long sunionstore(String dstkey, String... keys) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.sunionstore(dstkey, keys);
        }
    }

    @Override
    public String watch(String... keys) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.watch(keys);
        }
    }

    @Override
    public String unwatch() {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.unwatch();
        }
    }

    @Override
    public Long zinterstore(String dstkey, String... sets) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zinterstore(dstkey, sets);
        }
    }

    @Override
    public Long zunionstore(String dstkey, String... sets) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zunionstore(dstkey, sets);
        }
    }

    @Override
    public String brpoplpush(String source, String destination, int timeout) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.brpoplpush(source, destination, timeout);
        }
    }

    @Override
    public Long publish(String channel, String message) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.publish(channel, message);
        }
    }

    @Override
    public String randomKey() {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.randomKey();
        }
    }

    @Override
    public String pfmerge(String destkey, String... sourcekeys) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.pfmerge(destkey, sourcekeys);
        }
    }

    @Override
    public long pfcount(String... keys) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.pfcount(keys);
        }
    }

    @Override
    public Object eval(String script, int keyCount, String... params) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.eval(script, keyCount, params);
        }
    }

    @Override
    public Object eval(String script, List<String> keys, List<String> args) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.eval(script, keys, args);
        }
    }

    @Override
    public Object eval(String script) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.eval(script);
        }
    }

    @Override
    public Object evalsha(String script) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.evalsha(script);
        }
    }

    @Override
    public Object evalsha(String sha1, List<String> keys, List<String> args) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.evalsha(sha1, keys, args);
        }
    }

    @Override
    public Object evalsha(String sha1, int keyCount, String... params) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.evalsha(sha1, keyCount, params);
        }
    }

    @Override
    public Boolean scriptExists(String sha1) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.scriptExists(sha1);
        }
    }

    @Override
    public List<Boolean> scriptExists(String... sha1) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.scriptExists(sha1);
        }
    }

    @Override
    public String scriptLoad(String script) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.scriptLoad(script);
        }
    }
}
