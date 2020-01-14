package com.mwb.maf.core.web;

import lombok.Data;

@Data
public class CHttpBioClientProps {
    // 连接池中每个路由（目标host）的连接数
    private int defaultMaxPerRoute = 1000;
    // 连接池总的连接数
    private int maxTotal = defaultMaxPerRoute * 2;
    // 设置通过打开的连接传输数据的超时时间（单位：毫秒）
    private int soTimeout = 50 * 1000;
    /**
     * Defines period of inactivity in milliseconds after which persistent connections must
     * be re-validated prior to being {@link #leaseConnection(java.util.concurrent.Future,
     * long, java.util.concurrent.TimeUnit) leased} to the consumer. Non-positive value passed
     * to this method disables connection validation. This check helps detect connections
     * that have become stale (half-closed) while kept inactive in the pool.
     */
    private int validateAfterInactivity = 2 * 1000;

    /**
     * thread pool params
     */
    private int corePoolSize = 50;
    private int maximumPoolSize = 200;
    private long keepAliveTime = 60;
    private int capacity = 0;

    private int connIdleTimeout = 60;
}
