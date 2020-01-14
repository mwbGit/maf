package com.mwb.maf.core.db;

import com.alibaba.druid.pool.DruidDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

public abstract class AbstractDataSourceMybatisConfiguration extends BaseDataSourceConfiguration {

    abstract public DruidDataSource dataSource();

    public abstract PlatformTransactionManager txManager(DruidDataSource dataSource);

    public abstract SqlSessionFactoryBean sqlSessionFactory(DruidDataSource dataSource) throws Exception;
}
