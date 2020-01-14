package com.mwb.maf.core.rpc;

import com.mwb.maf.core.util.TracingUtils;
import com.weibo.api.motan.common.URLParamType;
import com.weibo.api.motan.core.extension.Activation;
import com.weibo.api.motan.core.extension.SpiMeta;
import com.weibo.api.motan.filter.Filter;
import com.weibo.api.motan.rpc.Caller;
import com.weibo.api.motan.rpc.Provider;
import com.weibo.api.motan.rpc.Request;
import com.weibo.api.motan.rpc.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpiMeta(name = "cafTracing")
@Activation(sequence = 30)
public class MotanTracingFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(MotanTracingFilter.class);

    @Override
    public Response filter(Caller<?> caller, Request request) {
        //String hostAddress = NetUtils.getLocalAddress().getHostAddress();
        if (caller instanceof Provider) {
            // comes from remote
            String source = request.getAttachments().get(URLParamType.host.getName());
            String traceId = request.getAttachments().get(TracingUtils.LABEL_TRACE_ID);
            if (StringUtils.isEmpty(traceId)) {
                traceId = System.currentTimeMillis() + "";
            }
            ThreadContext.put(TracingUtils.LABEL_TRACE_ID, traceId);
            ThreadContext.put(TracingUtils.LABEL_TRACE_SOURCE, source);
        } else {
            // go to call remote
            String traceId = ThreadContext.get(TracingUtils.LABEL_TRACE_ID);
            if (StringUtils.isNotEmpty(traceId)) {
                request.getAttachments().put(TracingUtils.LABEL_TRACE_ID, traceId);
            }
        }
        return caller.call(request);
    }
}
