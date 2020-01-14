package com.mwb.maf.core.kv;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(EnableJedisClusterClients.class)
@Import(JedisClusterClientRegistrar.class)
public @interface EnableJedisClusterClient {
    String namespace() default "default";
}