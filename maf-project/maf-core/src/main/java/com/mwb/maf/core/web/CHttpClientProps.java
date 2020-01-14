package com.mwb.maf.core.web;

import lombok.Data;

@Data
public class CHttpClientProps {
    /**
     * Returns the timeout in milliseconds used when requesting a connection from the connection manager.
     */
    private int connectionRequestTimeout = 100;
    /**
     * Defines the socket timeout (SO_TIMEOUT) in milliseconds, which is the timeout for waiting for data or, put differently, a maximum period inactivity between two consecutive data packets).
     */
    private int socketTimeout = 2000;
    /**
     * Determines the timeout in milliseconds until a connection is established.
     */
    private int connectTimeout = 2000;
    private int defaultMaxPerRoute = 50;
    private int maxTotal = defaultMaxPerRoute * 10;

    private int reactorIoThreadCount = Runtime.getRuntime().availableProcessors() > 8 ? 8 : Runtime.getRuntime().availableProcessors();
    private int reactorConnectTimeout = 1000;
    private int reactorSoTimeout = 1000;

    private int timeToLive = 60;

}
