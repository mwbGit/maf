package com.mwb.maf.core.rpc;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(EnableMotans.class)
@Import(MotanRegistrar.class)
public @interface EnableMotan {
    String namespace() default "default";
}