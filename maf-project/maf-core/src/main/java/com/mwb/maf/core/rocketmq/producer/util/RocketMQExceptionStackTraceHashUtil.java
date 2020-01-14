package com.mwb.maf.core.rocketmq.producer.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RocketMQExceptionStackTraceHashUtil {

    private static MessageDigest instance;

    static {
        try {
            instance = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static long hash(String str) {
        instance.reset();
        instance.update(str.getBytes());
        byte[] digest = instance.digest();

        long h = 0;
        for (int i = 0; i < 4; i++) {
            h <<= 8;
            h |= ((int) digest[i]) & 0xFF;
        }
        return h;
    }
}
