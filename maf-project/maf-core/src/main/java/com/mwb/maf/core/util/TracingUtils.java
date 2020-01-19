package com.mwb.maf.core.util;

import com.alibaba.fastjson.JSON;
import com.mwb.maf.core.logging.MafLog;
import com.mwb.maf.core.logging.MafRpcLog;
import com.mwb.maf.core.logging.MafWebLog;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.ThreadContext;

import java.util.UUID;

public class TracingUtils {
    public static final String LABEL_TRACE_ID = "traceId";
    public static final String LABEL_TRACE_LOG = "traceLog";

    public static String getTraceId() {
        return ThreadContext.get(TracingUtils.LABEL_TRACE_ID);
    }

    public static void putTraceId(String traceId) {
        ThreadContext.push(TracingUtils.LABEL_TRACE_ID, traceId);
    }

    public static void putTraceLog(MafLog mafLog) {
        ThreadContext.push(TracingUtils.LABEL_TRACE_LOG, JSON.toJSONString(mafLog));
    }

    public static MafRpcLog getRpcTraceLog() {
        String json = ThreadContext.get(TracingUtils.LABEL_TRACE_LOG);
        if (StringUtils.isNotEmpty(json)) {
            return JSON.parseObject(json, MafRpcLog.class);
        }
        return null;
    }

    public static MafWebLog geWebTraceLog() {
        String json = ThreadContext.get(TracingUtils.LABEL_TRACE_LOG);
        if (StringUtils.isNotEmpty(json)) {
            return JSON.parseObject(json, MafWebLog.class);
        }
        return null;
    }

    public static void cleanAll() {
        ThreadContext.clearAll();
    }

    public static String createTraceId() {
        return UUID.randomUUID().toString();
    }
}
