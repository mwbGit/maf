package com.mwb.maf.core.logging;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 描述:
 *
 * @author mengweibo@kanzhun.com
 * @create 2020/1/16
 */
@Getter
@Setter
public class MafLog implements Serializable {
    private static final long serialVersionUID = 7000204588376082531L;

    public static final int TYPE_WEB = 0;
    public static final int TYPE_RPC = 1;
    public static final int TYPE_DB = 2;

    private String traceId;

    private String serviceIp;

    private String requestIp;

    private Object[] requestParam;

    private int type;

    private String responseBody;

    private Long startTime;

    private Long endTime;

    private long processTime;

    private String exceptionStack;

    private String extra;


    public MafLog() {
        this.startTime = System.currentTimeMillis();
    }

    public MafLog(String traceId, String serviceIp, Object[] requestParam, String exceptionStack) {
        this.traceId = traceId;
        this.serviceIp = serviceIp;
        this.startTime = System.currentTimeMillis();
        this.requestParam = requestParam;
        this.exceptionStack = exceptionStack;
    }

    public MafLog(String traceId, String serviceIp, Object[] requestParam) {
        this.traceId = traceId;
        this.serviceIp = serviceIp;
        this.startTime = System.currentTimeMillis();
        this.requestParam = requestParam;
    }

    public void finish(){
        this.endTime = System.currentTimeMillis();
        this.processTime = this.endTime - this.startTime;

    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
