package com.mwb.maf.core.db;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@ConditionalOnClass({
        SqlSessionFactory.class,
})
public class DataSourceMybatisAutoConfiguration {

}
