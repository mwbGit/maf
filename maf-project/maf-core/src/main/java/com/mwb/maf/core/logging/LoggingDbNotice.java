package com.mwb.maf.core.logging;


import com.mwb.maf.core.logging.mapper.MafTraceLogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class LoggingDbNotice implements LoggingNotice {
    private static Logger logger = LoggerFactory.getLogger(LoggingDbNotice.class);

    @Autowired
    private MafTraceLogMapper mafTraceLogMapper;

    @Override
    public void notice(MafLog mafLog) {
        try {
            mafTraceLogMapper.insert(mafLog.getTraceId(), mafLog.getServiceIp(), mafLog.getStartTime(), mafLog.getEndTime(), mafLog.getProcessTime(), mafLog.toString(), mafLog.getType());
        } catch (Exception e) {
            logger.error("LoggingDbNotice insert db err mafLog={}", mafLog, e);
        }

    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }

}
