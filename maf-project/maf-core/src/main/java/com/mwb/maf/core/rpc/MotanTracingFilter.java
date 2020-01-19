package com.mwb.maf.core.rpc;

import com.alibaba.fastjson.JSONObject;
import com.mwb.maf.core.logging.Loggers;
import com.mwb.maf.core.logging.LoggingFactoryBean;
import com.mwb.maf.core.logging.LoggingNoticeEvent;
import com.mwb.maf.core.logging.MafRpcLog;
import com.mwb.maf.core.util.StackTraceUtil;
import com.mwb.maf.core.util.TracingUtils;
import com.weibo.api.motan.common.URLParamType;
import com.weibo.api.motan.core.extension.Activation;
import com.weibo.api.motan.core.extension.SpiMeta;
import com.weibo.api.motan.filter.Filter;
import com.weibo.api.motan.rpc.Caller;
import com.weibo.api.motan.rpc.Provider;
import com.weibo.api.motan.rpc.Request;
import com.weibo.api.motan.rpc.Response;
import com.weibo.api.motan.util.NetUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;


@SpiMeta(name = "cafTracing")
@Activation(sequence = 30)
@Component
public class MotanTracingFilter implements Filter {
    private static final Logger logger = Loggers.getTraceLogger();
    private static final Logger errorLogger = Loggers.getErrorLogger();

    @Override
    public Response filter(Caller<?> caller, Request request) {
        String hostAddress = NetUtils.getLocalAddress().getHostAddress();
        String traceId = TracingUtils.getTraceId();
        MafRpcLog mafRpcLog = null;
        try {
            mafRpcLog = new MafRpcLog(traceId, hostAddress, request.getArguments(), request.getRequestId(), request.getInterfaceName(), request.getMethodName(), request.getAttachments());
            if (caller instanceof Provider) {
                // 接到请求
                String source = request.getAttachments().get(URLParamType.host.getName());
                traceId = request.getAttachments().get(TracingUtils.LABEL_TRACE_ID);
                if (StringUtils.isEmpty(traceId)) {
                    traceId = TracingUtils.createTraceId();
                }
                mafRpcLog.setTraceId(traceId);
                mafRpcLog.setRequestIp(source);
            } else {
                // 发送请求
                if (StringUtils.isEmpty(traceId)) {
                    traceId = TracingUtils.createTraceId();
                }
                mafRpcLog.setTraceId(traceId);
                request.getAttachments().put(TracingUtils.LABEL_TRACE_ID, traceId);
            }
        } catch (Exception e) {
            errorLogger.error("rpc err traceId={}", traceId, e);
        }

        TracingUtils.putTraceId(traceId);
        Response response = null;
        try {
            response = caller.call(request);
        } finally {
            if (mafRpcLog == null) {
                mafRpcLog = new MafRpcLog();
                mafRpcLog.setTraceId(traceId);
            }
            if (response != null) {
                mafRpcLog.setResponseBody(JSONObject.toJSONString(response.getTimeout()));
                if (response.getException() != null) {
                    mafRpcLog.setExceptionStack(StackTraceUtil.getStackTrace(response.getException()));
                }
                mafRpcLog.finish();
            }
            LoggingFactoryBean.addPublishEvent(new LoggingNoticeEvent(this, mafRpcLog));
        }
        return response;
    }
}
