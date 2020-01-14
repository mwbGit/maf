package com.mwb.maf.core.web;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(CHttpBioClientRegistrar.class)
public @interface EnableCHttpBioClients {
    EnableCHttpBioClient[] value();
}