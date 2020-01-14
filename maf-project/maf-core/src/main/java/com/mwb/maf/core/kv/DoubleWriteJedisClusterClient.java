package com.mwb.maf.core.kv;

import com.alibaba.fastjson.TypeReference;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Tuple;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class DoubleWriteJedisClusterClient extends JedisClusterClient {
    private JedisClient jedisClient;

    public DoubleWriteJedisClusterClient(String namespace, JedisPoolConfig jedisPoolConfig, String address) throws NoSuchFieldException {
        super(namespace, jedisPoolConfig, address);
    }

    /******************************************************************************************************************/
    // write methods
    @Override
    public String set(String key, String value) {
        super.set(key, value);
        return jedisClient.set(key, value);
    }

    @Override
    public String set(String key, String value, String nxxx, String expx, long time) {
        super.set(key, value, nxxx, expx, time);
        return jedisClient.set(key, value, nxxx, expx, time);
    }

    @Override
    public String set(String key, String value, String nxxx) {
        super.set(key, value, nxxx);
        return jedisClient.set(key, value, nxxx);
    }

    @Override
    public Long persist(String key) {
        super.persist(key);
        return jedisClient.persist(key);
    }

    @Override
    public Long expire(String key, int seconds) {
        super.expire(key, seconds);
        return jedisClient.expire(key, seconds);
    }

    @Override
    public Long pexpire(String key, long milliseconds) {
        super.pexpire(key, milliseconds);
        return jedisClient.pexpire(key, milliseconds);
    }

    @Override
    public Long expireAt(String key, long unixTime) {
        super.expireAt(key, unixTime);
        return jedisClient.expireAt(key, unixTime);
    }

    @Override
    public Long pexpireAt(String key, long millisecondsTimestamp) {
        super.pexpireAt(key, millisecondsTimestamp);
        return jedisClient.pexpireAt(key, millisecondsTimestamp);
    }

    @Override
    public Boolean setbit(String key, long offset, boolean value) {
        super.setbit(key, offset, value);
        return jedisClient.setbit(key, offset, value);
    }

    @Override
    public Boolean setbit(String key, long offset, String value) {
        super.setbit(key, offset, value);
        return jedisClient.setbit(key, offset, value);
    }

    @Override
    public Long setrange(String key, long offset, String value) {
        super.setrange(key, offset, value);
        return jedisClient.setrange(key, offset, value);
    }

    @Override
    public String getSet(String key, String value) {
        super.getSet(key, value);
        return jedisClient.getSet(key, value);
    }

    @Override
    public Long setnx(String key, String value) {
        super.setnx(key, value);
        return jedisClient.setnx(key, value);
    }

    @Override
    public String setex(String key, int seconds, String value) {
        super.setex(key, seconds, value);
        return jedisClient.setex(key, seconds, value);
    }

    @Override
    public String psetex(String key, long milliseconds, String value) {
        super.psetex(key, milliseconds, value);
        return jedisClient.psetex(key, milliseconds, value);
    }

    @Override
    public Long decrBy(String key, long integer) {
        super.decrBy(key, integer);
        return jedisClient.decrBy(key, integer);
    }

    @Override
    public Long decr(String key) {
        super.decr(key);
        return jedisClient.decr(key);
    }

    @Override
    public Long incrBy(String key, long integer) {
        super.incrBy(key, integer);
        return jedisClient.incrBy(key, integer);
    }

    @Override
    public Double incrByFloat(String key, double value) {
        super.incrByFloat(key, value);
        return jedisClient.incrByFloat(key, value);
    }

    @Override
    public Long incr(String key) {
        super.incr(key);
        return jedisClient.incr(key);
    }

    @Override
    public Long append(String key, String value) {
        super.append(key, value);
        return jedisClient.append(key, value);
    }

    @Override
    public String substr(String key, int start, int end) {
        super.substr(key, start, end);
        return jedisClient.substr(key, start, end);
    }

    @Override
    public Long hset(String key, String field, String value) {
        super.hset(key, field, value);
        return jedisClient.hset(key, field, value);
    }

    @Override
    public Long hsetnx(String key, String field, String value) {
        super.hsetnx(key, field, value);
        return jedisClient.hsetnx(key, field, value);
    }

    @Override
    public String hmset(String key, Map<String, String> hash) {
        super.hmset(key, hash);
        return jedisClient.hmset(key, hash);
    }

    @Override
    public Long hincrBy(String key, String field, long value) {
        super.hincrBy(key, field, value);
        return jedisClient.hincrBy(key, field, value);
    }

    @Override
    public Double hincrByFloat(String key, String field, double value) {
        super.hincrByFloat(key, field, value);
        return jedisClient.hincrByFloat(key, field, value);
    }

    @Override
    public Long hdel(String key, String... field) {
        super.hdel(key, field);
        return jedisClient.hdel(key, field);
    }

    @Override
    public Long rpush(String key, String... string) {
        super.rpush(key, string);
        return jedisClient.rpush(key, string);
    }

    @Override
    public Long lpush(String key, String... string) {
        super.lpush(key, string);
        return jedisClient.lpush(key, string);
    }


    @Override
    public String lset(String key, long index, String value) {
        super.lset(key, index, value);
        return jedisClient.lset(key, index, value);
    }

    @Override
    public Long lrem(String key, long count, String value) {
        super.lrem(key, count, value);
        return jedisClient.lrem(key, count, value);
    }

    @Override
    public String lpop(String key) {
        super.lpop(key);
        return jedisClient.lpop(key);
    }

    @Override
    public String rpop(String key) {
        super.rpop(key);
        return jedisClient.rpop(key);
    }

    @Override
    public Long sadd(String key, String... member) {
        super.sadd(key, member);
        return jedisClient.sadd(key, member);
    }

    @Override
    public Set<String> smembers(String key) {
        return jedisClient.smembers(key);
    }

    @Override
    public Long srem(String key, String... member) {
        super.srem(key, member);
        return jedisClient.srem(key, member);
    }

    @Override
    public String spop(String key) {
        super.spop(key);
        return jedisClient.spop(key);
    }

    @Override
    public Set<String> spop(String key, long count) {
        super.spop(key, count);
        return jedisClient.spop(key, count);
    }

    @Override
    public Long scard(String key) {
        super.scard(key);
        return jedisClient.scard(key);
    }

    @Override
    public Long zadd(String key, double score, String member) {
        super.zadd(key, score, member);
        return jedisClient.zadd(key, score, member);
    }

    @Override
    public Long zadd(String key, Map<String, Double> scoreMembers) {
        super.zadd(key, scoreMembers);
        return jedisClient.zadd(key, scoreMembers);
    }

    @Override
    public Long zremrangeByLex(String key, String min, String max) {
        super.zremrangeByLex(key, min, max);
        return jedisClient.zremrangeByLex(key, min, max);
    }

    @Override
    public Long lpushx(String key, String... string) {
        super.lpushx(key, string);
        return jedisClient.lpushx(key, string);
    }

    @Override
    public Long rpushx(String key, String... string) {
        super.rpushx(key, string);
        return jedisClient.rpushx(key, string);
    }

    @Override
    public List<String> blpop(int timeout, String key) {
        super.lpop(key);
        return jedisClient.blpop(timeout, key);
    }

    @Override
    public List<String> brpop(int timeout, String key) {
        super.rpop(key);
        return jedisClient.brpop(timeout, key);
    }

    @Override
    public Long del(String key) {
        super.del(key);
        return jedisClient.del(key);
    }

    @Override
    public Long zremrangeByRank(String key, long start, long end) {
        super.zremrangeByRank(key, start, end);
        return jedisClient.zremrangeByRank(key, start, end);
    }

    @Override
    public Long zremrangeByScore(String key, double start, double end) {
        super.zremrangeByScore(key, start, end);
        return jedisClient.zremrangeByScore(key, start, end);
    }

    @Override
    public Long zremrangeByScore(String key, String start, String end) {
        super.zremrangeByScore(key, start, end);
        return jedisClient.zremrangeByScore(key, start, end);
    }

    @Override
    public String ltrim(String key, long start, long end) {
        super.ltrim(key, start, end);
        return jedisClient.ltrim(key, start, end);
    }

    @Override
    public Long zrem(String key, String... member) {
        super.zrem(key, member);
        return jedisClient.zrem(key, member);
    }

    @Override
    public Double zincrby(String key, double score, String member) {
        super.zincrby(key, score, member);
        return jedisClient.zincrby(key, score, member);
    }

    @Override
    public Long zcard(String key) {
        super.zcard(key);
        return jedisClient.zcard(key);
    }

    @Override
    public Long move(String key, int dbIndex) {
        super.move(key, dbIndex);
        return jedisClient.move(key, dbIndex);
    }

    @Override
    public Object eval(String script, int keyCount, String... params) {
        super.eval(script, keyCount, params);
        return jedisClient.eval(script, keyCount, params);
    }

    @Override
    public Object eval(String script, List<String> keys, List<String> args) {
        super.eval(script, keys, args);
        return jedisClient.eval(script, keys, args);
    }

    @Override
    public Object evalsha(String sha1, List<String> keys, List<String> args) {
        super.evalsha(sha1, keys, args);
        return jedisClient.evalsha(sha1, keys, args);
    }

    @Override
    public Object evalsha(String sha1, int keyCount, String... params) {
        super.evalsha(sha1, keyCount, params);
        return jedisClient.evalsha(sha1, keyCount, params);
    }

    @Override
    public boolean setCache(String key, Object obj) {
        super.setCache(key, obj);
        return jedisClient.setCache(key, obj);
    }

    @Override
    public boolean setCache(String key, Object obj, int timeout) {
        super.setCache(key, obj, timeout);
        return jedisClient.setCache(key, obj, timeout);
    }

    @Override
    public <T> T getCache(String key, TypeReference<T> typeReference) {
        return jedisClient.getCache(key, typeReference);
    }

    /******************************************************************************************************************/
    // read methods
    @Override
    public String get(String key) {
        return jedisClient.get(key);
    }

    @Override
    public Boolean exists(String key) {
        return jedisClient.exists(key);
    }

    @Override
    public String type(String key) {
        return jedisClient.type(key);
    }

    @Override
    public Long ttl(String key) {
        return super.ttl(key);
    }

    @Override
    public Long pttl(String key) {
        return super.pttl(key);
    }

    @Override
    public Boolean getbit(String key, long offset) {
        return jedisClient.getbit(key, offset);
    }

    @Override
    public String getrange(String key, long startOffset, long endOffset) {
        return jedisClient.getrange(key, startOffset, endOffset);
    }

    @Override
    public String hget(String key, String field) {
        return jedisClient.hget(key, field);
    }

    @Override
    public List<String> hmget(String key, String... fields) {
        return jedisClient.hmget(key, fields);
    }

    @Override
    public Boolean hexists(String key, String field) {
        return jedisClient.hexists(key, field);
    }

    @Override
    public Long hlen(String key) {
        return jedisClient.hlen(key);
    }

    @Override
    public Set<String> hkeys(String key) {
        return jedisClient.hkeys(key);
    }

    @Override
    public List<String> hvals(String key) {
        return jedisClient.hvals(key);
    }

    @Override
    public Map<String, String> hgetAll(String key) {
        return jedisClient.hgetAll(key);
    }

    @Override
    public Long llen(String key) {
        return jedisClient.llen(key);
    }

    @Override
    public List<String> lrange(String key, long start, long end) {
        return jedisClient.lrange(key, start, end);
    }


    @Override
    public String lindex(String key, long index) {
        return jedisClient.lindex(key, index);
    }


    @Override
    public Boolean sismember(String key, String member) {
        return jedisClient.sismember(key, member);
    }

    @Override
    public String srandmember(String key) {
        return jedisClient.srandmember(key);
    }

    @Override
    public List<String> srandmember(String key, int count) {
        return jedisClient.srandmember(key, count);
    }

    @Override
    public Long strlen(String key) {
        return jedisClient.strlen(key);
    }

    @Override
    public Set<String> zrange(String key, long start, long end) {
        return jedisClient.zrange(key, start, end);
    }


    @Override
    public Long zrank(String key, String member) {
        return jedisClient.zrank(key, member);
    }

    @Override
    public Long zrevrank(String key, String member) {
        return jedisClient.zrevrank(key, member);
    }

    @Override
    public Set<String> zrevrange(String key, long start, long end) {
        return jedisClient.zrevrange(key, start, end);
    }

    @Override
    public Double zscore(String key, String member) {
        return jedisClient.zscore(key, member);
    }

    @Override
    public List<String> sort(String key) {
        return jedisClient.sort(key);
    }

    @Override
    public Long zcount(String key, double min, double max) {
        return jedisClient.zcount(key, min, max);
    }

    @Override
    public Long zcount(String key, String min, String max) {
        return jedisClient.zcount(key, min, max);
    }

    @Override
    public Set<String> zrangeByScore(String key, double min, double max) {
        return jedisClient.zrangeByScore(key, min, max);
    }

    @Override
    public Set<String> zrangeByScore(String key, String min, String max) {
        return jedisClient.zrangeByScore(key, min, max);
    }

    @Override
    public Set<String> zrevrangeByScore(String key, double max, double min) {
        return jedisClient.zrevrangeByScore(key, max, min);
    }

    @Override
    public Set<String> zrangeByScore(String key, double min, double max, int offset, int count) {
        return jedisClient.zrangeByScore(key, min, max, offset, count);
    }

    @Override
    public Set<String> zrevrangeByScore(String key, String max, String min) {
        return jedisClient.zrevrangeByScore(key, max, min);
    }

    @Override
    public Set<String> zrangeByScore(String key, String min, String max, int offset, int count) {
        return jedisClient.zrangeByScore(key, min, max, offset, count);
    }

    @Override
    public Set<String> zrevrangeByScore(String key, double max, double min, int offset, int count) {
        return jedisClient.zrevrangeByScore(key, max, min, offset, count);
    }

    @Override
    public Set<String> zrevrangeByScore(String key, String max, String min, int offset, int count) {
        return jedisClient.zrevrangeByScore(key, max, min, offset, count);
    }


    @Override
    public Long zlexcount(String key, String min, String max) {
        return jedisClient.zlexcount(key, min, max);
    }

    @Override
    public Set<String> zrangeByLex(String key, String min, String max) {
        return jedisClient.zrangeByLex(key, min, max);
    }

    @Override
    public Set<String> zrangeByLex(String key, String min, String max, int offset, int count) {
        return jedisClient.zrangeByLex(key, min, max, offset, count);
    }

    @Override
    public Set<String> zrevrangeByLex(String key, String max, String min) {
        return jedisClient.zrevrangeByLex(key, max, min);
    }

    @Override
    public Set<String> zrevrangeByLex(String key, String max, String min, int offset, int count) {
        return jedisClient.zrevrangeByLex(key, max, min, offset, count);
    }


    @Override
    public Long bitcount(String key) {
        return jedisClient.bitcount(key);
    }

    @Override
    public Long bitcount(String key, long start, long end) {
        return jedisClient.bitcount(key, start, end);
    }

    @Override
    public Long bitpos(String key, boolean value) {
        return jedisClient.bitpos(key, value);
    }

    @Override
    public Long pfadd(String key, String... elements) {
        super.pfadd(key, elements);
        return jedisClient.pfadd(key, elements);
    }

    @Override
    public long pfcount(String key) {
        return jedisClient.pfcount(key);
    }

    @Override
    public Long geoadd(String key, double longitude, double latitude, String member) {
        return jedisClient.geoadd(key, longitude, latitude, member);
    }

    @Override
    public Double geodist(String key, String member1, String member2) {
        return jedisClient.geodist(key, member1, member2);
    }

    @Override
    public List<String> geohash(String key, String... members) {
        return jedisClient.geohash(key, members);
    }

    @Override
    public Object eval(String script, String key) {
        return super.eval(script, key);
    }

    @Override
    public Object evalsha(String script, String key) {
        return super.evalsha(script, key);
    }

    @Override
    public List<Long> bitfield(String key, String... arguments) {
        return jedisClient.bitfield(key, arguments);
    }

    @Override
    public String echo(String string) {
        return jedisClient.echo(string);
    }


    @Override
    public Boolean scriptExists(String sha1, String key) {
        return super.scriptExists(sha1, key);
    }

    @Override
    public List<Boolean> scriptExists(String key, String... sha1) {
        return super.scriptExists(key, sha1);
    }

    @Override
    public String scriptLoad(String script, String key) {
        return super.scriptLoad(script, key);
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max) {
        return super.zrangeByScoreWithScores(key, min, max);
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min) {
        return super.zrevrangeByScoreWithScores(key, max, min);
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max, int offset, int count) {
        return super.zrangeByScoreWithScores(key, min, max, offset, count);
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max) {
        return super.zrangeByScoreWithScores(key, min, max);
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min) {
        return super.zrevrangeByScoreWithScores(key, max, min);
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max, int offset, int count) {
        return super.zrangeByScoreWithScores(key, min, max, offset, count);
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min, int offset, int count) {
        return super.zrevrangeByScoreWithScores(key, max, min, offset, count);
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min, int offset, int count) {
        return super.zrevrangeByScoreWithScores(key, max, min, offset, count);
    }

    @Override
    public Set<Tuple> zrangeWithScores(String key, long start, long end) {
        return super.zrangeWithScores(key, start, end);
    }
}
