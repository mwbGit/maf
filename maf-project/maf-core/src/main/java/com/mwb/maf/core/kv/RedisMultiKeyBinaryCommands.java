package com.mwb.maf.core.kv;

import java.util.List;
import java.util.Set;

public interface RedisMultiKeyBinaryCommands {
    Long del(byte[]... keys);

    Long exists(byte[]... keys);

    List<byte[]> blpop(int timeout, byte[]... keys);

    List<byte[]> brpop(int timeout, byte[]... keys);

    List<byte[]> blpop(byte[]... args);

    List<byte[]> brpop(byte[]... args);

    Set<byte[]> keys(byte[] pattern);

    List<byte[]> mget(byte[]... keys);

    String mset(byte[]... keysvalues);

    Long msetnx(byte[]... keysvalues);

    String rename(byte[] oldkey, byte[] newkey);

    Long renamenx(byte[] oldkey, byte[] newkey);

    byte[] rpoplpush(byte[] srckey, byte[] dstkey);

    Set<byte[]> sdiff(byte[]... keys);

    Long sdiffstore(byte[] dstkey, byte[]... keys);

    Set<byte[]> sinter(byte[]... keys);

    Long sinterstore(byte[] dstkey, byte[]... keys);

    Long smove(byte[] srckey, byte[] dstkey, byte[] member);

    Long sort(byte[] key, byte[] dstkey);

    Set<byte[]> sunion(byte[]... keys);

    Long sunionstore(byte[] dstkey, byte[]... keys);

    String watch(byte[]... keys);

    String unwatch();

    Long zinterstore(byte[] dstkey, byte[]... sets);

    Long zunionstore(byte[] dstkey, byte[]... sets);

    byte[] brpoplpush(byte[] source, byte[] destination, int timeout);

    Long publish(byte[] channel, byte[] message);

    byte[] randomBinaryKey();

    String pfmerge(final byte[] destkey, final byte[]... sourcekeys);

    Long pfcount(byte[]... keys);
}
