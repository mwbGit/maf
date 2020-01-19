package com.mwb.maf.core.logging;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(TraceLogRegistrar.class)
public @interface EnableTraceLog {

    String namespace() default "default";

    boolean write() default true;
}