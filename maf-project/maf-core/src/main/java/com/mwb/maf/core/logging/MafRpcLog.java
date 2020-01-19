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
public class MafRpcLog extends MafLog implements Serializable {
    private static final long serialVersionUID = 7000204588376082531L;

    private long requestId;

    private String interfaceName;

    private String methodName;

    private Map<String, String> attachments;

    public MafRpcLog() {
        this.setType(MafLog.TYPE_RPC);
    }

    public MafRpcLog(String traceId, String serviceIp, Object[] requestParam, long requestId, String interfaceName, String methodName, Map<String, String> attachments) {
        super(traceId, serviceIp, requestParam);
        this.requestId = requestId;
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.attachments = attachments;
        this.setType(MafLog.TYPE_RPC);
    }

    public MafRpcLog(String traceId, String serviceIp, Object[] requestParam, String exceptionStack, long requestId, String interfaceName, String methodName, Map<String, String> attachments) {
        super(traceId, serviceIp, requestParam, exceptionStack);
        this.requestId = requestId;
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.attachments = attachments;
        this.setType(MafLog.TYPE_RPC);
    }
}
