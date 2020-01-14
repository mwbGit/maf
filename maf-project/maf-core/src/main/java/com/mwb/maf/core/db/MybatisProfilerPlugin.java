package com.mwb.maf.core.db;

import com.mwb.maf.core.metrics.LatencyProfiler;
import com.mwb.maf.core.metrics.LatencyStat;
import com.mwb.maf.core.metrics.MonitorConfig;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.Properties;


@Intercepts(
        value = {
                @Signature(type = Executor.class,
                        method = "update",
                        args = {MappedStatement.class, Object.class}),
                @Signature(type = Executor.class,
                        method = "query",
                        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class,
                                CacheKey.class, BoundSql.class}),
                @Signature(type = Executor.class,
                        method = "query",
                        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
        }
)
public class MybatisProfilerPlugin implements Interceptor {

    private static final LatencyStat MYBATIS_STAT = LatencyProfiler.Builder.build()
            .tag("mapper")
            .name("mapper")
            .defineLabels("operation", "class")
            .buckets(
                    0.0001, 0.0005, 0.0009,
                    0.001, 0.003, 0.005, 0.007, 0.009,
                    0.01, 0.03, 0.05, 0.07, 0.09,
                    0.1, 0.3, 0.5, 0.7, 0.9,
                    1, 3, 5)
            .create();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (!MonitorConfig.DYNAMIC_ENABLE_MAPPER_PROFILE) {
            return invocation.proceed();
        }
        final Object[] args = invocation.getArgs();
        if (args != null && args.length > 0) {
            final MappedStatement mappedStatement = (MappedStatement) args[0];
            if (mappedStatement != null) {
                final String methodName = mappedStatement.getId();
                final String declaringTypeName = mappedStatement.getResource();
                final LatencyStat.Timer timer = MYBATIS_STAT.startTimer(methodName, declaringTypeName);
                try {
                    MYBATIS_STAT.inc(methodName, declaringTypeName);
                    return invocation.proceed();
                } catch (Throwable throwable) {
                    MYBATIS_STAT.error(methodName, declaringTypeName);
                    throw throwable;
                } finally {
                    timer.observeDuration();
                    MYBATIS_STAT.dec(methodName, declaringTypeName);
                }
            }
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}