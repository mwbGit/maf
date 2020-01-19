package com.mwb.app.sample.db.logging;


import com.mwb.maf.core.logging.LoggingNotice;
import com.mwb.maf.core.logging.MafLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 链路日志处理
 */
@Slf4j
@Component
public class LoggingLocalNotice implements LoggingNotice {

    @Override
    public void notice(MafLog mafLog) {
        // todo something
        log.info("LoggingLocalNotice mafLog={}", mafLog);
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }

}
