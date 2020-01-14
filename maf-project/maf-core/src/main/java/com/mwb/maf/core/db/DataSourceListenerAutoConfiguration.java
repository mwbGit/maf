package com.mwb.maf.core.db;

import com.alibaba.druid.pool.DruidDataSource;
import com.mwb.maf.core.AbstractConfigPrintSpringListener;
import com.mwb.maf.core.logging.Loggers;
import com.mwb.maf.core.util.LogUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.util.Map;

@ConditionalOnClass({
        SqlSessionFactory.class,
        DruidDataSource.class
})
@ConditionalOnBean(DruidDataSource.class)
public class DataSourceListenerAutoConfiguration {
    private static final Logger logger = Loggers.getFrameworkLogger();

    @Bean
    public ApplicationListener<ApplicationEvent> dataSourceConfigListener() {
        return new AbstractConfigPrintSpringListener<DruidDataSource>() {
            @Override
            protected Class<DruidDataSource> getConfigClass() {
                return DruidDataSource.class;
            }

            @Override
            protected String getModuleName() {
                return "DataSource";
            }

            @Override
            protected void logConfigInfo(Map.Entry<String, DruidDataSource> entry) {
                /**
                 * @See {@link BaseDataSourceConfiguration}
                 * https://github.com/alibaba/druid/wiki/DruidDataSource%E9%85%8D%E7%BD%AE%E5%B1%9E%E6%80%A7%E5%88%97%E8%A1%A8
                 */
                DruidDataSource dataSource = entry.getValue();
                if (dataSource == null)
                    return;
                LogUtils.putContextColumn1("config");
                LogUtils.putContextColumn2("druid:" +
                        StringUtils.substringBefore(entry.getKey(), DataSource.class.getSimpleName()) +
                        ":" + DateTime.now().toString("yyyyMMddHHmmss"));
                logger.info("{} - {}", "beanName", entry.getKey());
                logger.info("\t{} - {}", "id", dataSource.getID());
                logger.info("\t{} - {}", "name", dataSource.getName());
                logger.info("\t{} - {}", "jdbcUrl", dataSource.getUrl());
                logger.info("\t{} - {}", "username", dataSource.getUsername());
                logger.info("\t{} - {}", "driverClass", dataSource.getDriverClassName());
                logger.info("\t{} - {}", "initialSize", dataSource.getInitialSize());
                logger.info("\t{} - {}", "maxActive", dataSource.getMaxActive());
                logger.info("\t{} - {}", "maxIdle", dataSource.getMaxIdle());
                logger.info("\t{} - {}", "minIdle", dataSource.getMinIdle());
                logger.info("\t{} - {}", "maxWait", dataSource.getMaxWait());
                logger.info("\t{} - {}", "poolPreparedStatements", dataSource.isPoolPreparedStatements());
                logger.info("\t{} - {}", "validationQuery", dataSource.getValidationQuery());
                logger.info("\t{} - {}", "validationQueryTimeout", dataSource.getValidationQueryTimeout());
                logger.info("\t{} - {}", "testOnBorrow", dataSource.isTestOnBorrow());
                logger.info("\t{} - {}", "testOnReturn", dataSource.isTestOnReturn());
                logger.info("\t{} - {}", "testWhileIdle", dataSource.isTestWhileIdle());
                logger.info("\t{} - {}", "timeBetweenEvictionRunsMillis", dataSource.getTimeBetweenEvictionRunsMillis());
                logger.info("\t{} - {}", "minEvictableIdleTimeMillis", dataSource.getMinEvictableIdleTimeMillis());
                logger.info("\t{} - {}", "connectionInitSqls", dataSource.getConnectionInitSqls());
                logger.info("\t{} - {}", "filters", dataSource.getFilterClassNames());
                logger.info(LogUtils.LINE);
                LogUtils.clearContext();

            }
        };
    }
}
