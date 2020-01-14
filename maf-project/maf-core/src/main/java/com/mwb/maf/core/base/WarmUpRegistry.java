package com.mwb.maf.core.base;

import com.google.common.collect.Sets;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;
import java.util.Set;

public class WarmUpRegistry implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    public Set<WarmUp> warmUps() {
        Set<WarmUp> warmUps = Sets.newConcurrentHashSet();
        final Map<String, WarmUp> beansOfType = applicationContext.getBeansOfType(WarmUp.class);
        warmUps.clear();
        warmUps.addAll(beansOfType.values());
        return warmUps;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
