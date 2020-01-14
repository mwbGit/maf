package com.mwb.maf.core.util;

import io.prometheus.client.Collector;

public class TimeUtils {
    public static double elapsedSecondsFromNanos(long startNanos, long endNanos) {
        return elapsedSeconds(endNanos - startNanos);
    }

    public static double elapsedSeconds(long elapsedNanos) {
        return elapsedNanos / Collector.NANOSECONDS_PER_SECOND;
    }
}
