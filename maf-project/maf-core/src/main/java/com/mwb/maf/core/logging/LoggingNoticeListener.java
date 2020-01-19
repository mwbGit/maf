/*
 * Copyright [2019] [恒宇少年 - 于起宇]
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 *
 */

package com.mwb.maf.core.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.core.OrderComparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 描述:
 *
 * @author mengweibo@kanzhun.com
 * @create 2020/1/16
 */
public class LoggingNoticeListener implements SmartApplicationListener, ApplicationContextAware {
    private static Logger logger = LoggerFactory.getLogger(LoggingNoticeListener.class);
    private ApplicationContext applicationContext;

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return eventType == LoggingNoticeEvent.class;
    }

    @Override
    @Async
    public void onApplicationEvent(ApplicationEvent event) {
        LoggingNoticeEvent loggingNoticeEvent = (LoggingNoticeEvent) event;
        MafLog mafLog = loggingNoticeEvent.getLog();
        Map<String, LoggingNotice> noticeMap = applicationContext.getBeansOfType(LoggingNotice.class);
        if (ObjectUtils.isEmpty(noticeMap)) {
            return;
        }
        List<LoggingNotice> noticeList = new ArrayList<>(noticeMap.values());
        OrderComparator.sort(noticeList);
        noticeList.forEach(loggingNotice -> loggingNotice.notice(mafLog));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        Assert.notNull(applicationContext, "ApplicationContext must not be null");

    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }
}
