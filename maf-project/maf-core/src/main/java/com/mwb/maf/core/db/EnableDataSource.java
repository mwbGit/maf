package com.mwb.maf.core.db;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(EnableDataSources.class)
@Import(DataSourceRegistrar.class)
public @interface EnableDataSource {
    String namespace() default "default";

    String[] mapperPackages() default {};
}