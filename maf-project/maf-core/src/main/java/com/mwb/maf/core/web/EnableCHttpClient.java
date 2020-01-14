package com.mwb.maf.core.web;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(EnableCHttpClients.class)
@Import(CHttpClientRegistrar.class)
public @interface EnableCHttpClient {
    String namespace() default "default";
}