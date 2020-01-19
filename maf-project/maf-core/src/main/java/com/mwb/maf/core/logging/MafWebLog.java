package com.mwb.maf.core.logging;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

/**
 * 描述:
 *
 * @author mengweibo@kanzhun.com
 * @create 2020/1/16
 */
@Getter
@Setter
public class MafWebLog extends MafLog implements Serializable {
    private static final long serialVersionUID = 7000204588376082131L;

    private String requestUri;

    private String requestMethod;

    private Map<String, String> requestHeaders;

    private String requestBody;

    private Map<String, String> responseHeaders;

    private String responseBody;

    private int responseStatus;

    public MafWebLog() {
        this.setStartTime(System.currentTimeMillis());
    }

    public MafWebLog(String traceId, String serviceIp, String requestParam, String requestUri, String requestMethod, Map<String, String> requestHeaders, String requestBody, String requestIp) {
        super(traceId, serviceIp, new Object[]{requestParam});
        this.requestUri = requestUri;
        this.requestMethod = requestMethod;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
        this.setRequestIp(requestIp);
    }

    public void setResponse(int responseStatus, Map<String, String> responseHeaders, String responseBody) {
        this.responseStatus = responseStatus;
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
        this.setEndTime(System.currentTimeMillis());
        this.setProcessTime(getEndTime() - getStartTime());
    }
}
