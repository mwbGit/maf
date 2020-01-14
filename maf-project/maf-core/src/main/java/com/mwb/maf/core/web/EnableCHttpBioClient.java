package com.mwb.maf.core.web;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(EnableCHttpBioClients.class)
@Import(CHttpBioClientRegistrar.class)
public @interface EnableCHttpBioClient {
    String namespace() default "default";
}