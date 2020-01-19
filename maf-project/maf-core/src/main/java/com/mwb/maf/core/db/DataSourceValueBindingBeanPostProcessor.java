package com.mwb.maf.core.db;

import com.alibaba.druid.filter.logging.Log4j2Filter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.mwb.maf.core.util.CustomizedConfigurationPropertiesBinder;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.util.Assert;

import javax.sql.DataSource;

public class DataSourceValueBindingBeanPostProcessor extends BaseDataSourceConfiguration implements BeanPostProcessor, Ordered, EnvironmentAware, BeanFactoryAware {

    @Autowired
    protected CustomizedConfigurationPropertiesBinder binder;
    private Environment environment;
    private BeanFactory beanFactory;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        if (bean instanceof DruidDataSource) {
            DruidDataSource druidDataSource = (DruidDataSource) bean;
            String namespace = StringUtils.substringBefore(beanName, DataSource.class.getSimpleName());
            initDataSource(druidDataSource);
            if (environment.containsProperty(PREFIX_APP_DATASOURCE + "." + namespace + ".data-source" + ".filters")) {
                druidDataSource.clearFilters();
            }
            Bindable<?> target = Bindable.of(DruidDataSource.class).withExistingValue(druidDataSource);
            String url = PREFIX_APP_DATASOURCE + "." + namespace + ".data-source.url";
            String urlVal = environment.getProperty(url);
            Assert.isTrue(StringUtils.isNotEmpty(urlVal), String.format("%s=%s must be not null! ", url, urlVal));
            binder.bind(PREFIX_APP_DATASOURCE + "." + namespace + ".data-source", target);

        } else if (bean instanceof SqlSessionFactoryBean) {
            SqlSessionFactoryBean sqlSessionFactoryBean = (SqlSessionFactoryBean) bean;
            String namespace = StringUtils.substringBefore(beanName, SqlSessionFactory.class.getSimpleName());

            DataSource dataSource = beanFactory.getBean(namespace + DataSource.class.getSimpleName(), DataSource.class);
            String typeAliasesPackageKey = PREFIX_APP_DATASOURCE + "." + namespace + ".type-aliases-package";
            String typeAliasesPackage = environment.getProperty(typeAliasesPackageKey);
            initSqlSessionFactoryBean(dataSource, typeAliasesPackage, sqlSessionFactoryBean);

        } else if (bean instanceof DataSourceTransactionManager) {
            DataSourceTransactionManager dataSourceTransactionManager = (DataSourceTransactionManager) bean;

            String namespace = StringUtils.substringBefore(beanName, DataSourceTransactionManager.class.getSimpleName());

            DataSource dataSource = beanFactory.getBean(namespace + DataSource.class.getSimpleName(), DataSource.class);

            dataSourceTransactionManager.setDataSource(dataSource);
            dataSourceTransactionManager.afterPropertiesSet();
        } else if (bean instanceof Log4j2Filter) {
//            String namespace = StringUtils.substringBefore(beanName, Log4j2Filter.class.getSimpleName());
//            Log4j2Filter log4jFilter = (Log4j2Filter) bean;
//            initLog4jFilter(log4jFilter);
//
//            Bindable<?> target = Bindable.of(Log4j2Filter.class).withExistingValue(log4jFilter);
//            binder.bind(PREFIX_APP_DATASOURCE + "." + namespace + ".log4j2", target);

//            Log4j2Filter log4j2Filter = beanFactory.getBean(namespace + Log4j2Filter.class.getSimpleName(), Log4j2Filter.class);
//            if (log4j2Filter != null) {
//            druidDataSource.getProxyFilters().add(log4j2Filter);
//            }
        } else if (bean instanceof StatFilter) {
            String namespace = StringUtils.substringBefore(beanName, StatFilter.class.getSimpleName());
            String enabled = environment.getProperty(PREFIX_APP_DATASOURCE + "." + namespace + ".data-source.stat.enabled");
            if (!"true".equalsIgnoreCase(enabled)) {
                return bean;
            }
            StatFilter statFilter = (StatFilter) bean;
            initStatFilter(statFilter);
            Bindable<?> target = Bindable.of(StatFilter.class).withExistingValue(statFilter);
            binder.bind(PREFIX_APP_DATASOURCE + "." + namespace + ".data-source.stat", target);

            DruidDataSource druidDataSource = beanFactory.getBean(namespace + DataSource.class.getSimpleName(), DruidDataSource.class);
            if (druidDataSource != null) {
                druidDataSource.setStatLogger(new CustomizedDruidDataSourceStatLogger());
                if (druidDataSource.getStatLogger() != null &&
                        druidDataSource.getStatLogger() instanceof CustomizedDruidDataSourceStatLogger) {
                    ((CustomizedDruidDataSourceStatLogger) druidDataSource.getStatLogger()).setNamespace(namespace);
                }
                druidDataSource.getProxyFilters().add(statFilter);
            }
        }
        return bean;
    }


    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
