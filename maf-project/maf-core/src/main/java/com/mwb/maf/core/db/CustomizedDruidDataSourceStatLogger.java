package com.mwb.maf.core.db;


import com.alibaba.druid.pool.DruidDataSourceStatLoggerAdapter;
import com.alibaba.druid.pool.DruidDataSourceStatValue;
import com.alibaba.druid.stat.JdbcSqlStatValue;
import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSON;
import com.mwb.maf.core.logging.Loggers;
import com.mwb.maf.core.util.LogUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import static com.alibaba.druid.util.JdbcSqlStatUtils.rtrim;

public class CustomizedDruidDataSourceStatLogger extends DruidDataSourceStatLoggerAdapter {
    private String namespace;
    private Logger LOG = Loggers.getPerformanceLogger();
    private volatile AtomicLong counter = new AtomicLong();

    public void setLogger(Logger logger) {
        this.LOG = logger;
    }

    private Logger logger() {
        return LOG;
    }

    private boolean isLogEnable() {
        return LOG.isInfoEnabled();
    }

    @Override
    public void log(DruidDataSourceStatValue statValue) {
        if (!isLogEnable()) {
            return;
        }
        LogUtils.putContextColumn1("health");
        LogUtils.putContextColumn2("druid" + ":" + getNamespace() + ":" + DateTime.now().toString("yyyyMMddHHmmss"));
        logger().info(LogUtils.LINE);
        Map<String, Object> gaugeMap = new LinkedHashMap<String, Object>();
        fillGaugeValue(statValue, gaugeMap);
        if (MapUtils.isNotEmpty(gaugeMap)) {
            gaugeMap.forEach((key, value) -> logger().info("{} : {}", key, value));
        }

        if (statValue.getSqlList().size() > 0) {
            int i = 0;
            for (JdbcSqlStatValue sqlStat : statValue.getSqlList()) {
                if (i > 100)
                    break;
                Map<String, Object> sqlStatMap = new LinkedHashMap<String, Object>();
                fillSqlStatMap(sqlStat, sqlStatMap);
                logger().info(JSONUtils.toJSONString(sqlStatMap));
                i++;
            }
        }

        logger().info(LogUtils.LINE);
        LogUtils.clearContext();
    }

    private void fillSqlStatMap(JdbcSqlStatValue sqlStat, Map<String, Object> sqlStatMap) {
        sqlStatMap.put("sql", sqlStat.getSql());

        if (sqlStat.getExecuteCount() > 0) {
            sqlStatMap.put("executeCount", sqlStat.getExecuteCount());
            sqlStatMap.put("executeMillisMax", sqlStat.getExecuteMillisMax());
            sqlStatMap.put("executeMillisTotal", sqlStat.getExecuteMillisTotal());

            sqlStatMap.put("executeHistogram", rtrim(sqlStat.getExecuteHistogram()));
            sqlStatMap.put("executeAndResultHoldHistogram", rtrim(sqlStat.getExecuteAndResultHoldHistogram()));
        }

        long executeErrorCount = sqlStat.getExecuteErrorCount();
        if (executeErrorCount > 0) {
            sqlStatMap.put("executeErrorCount", executeErrorCount);
        }

        int runningCount = sqlStat.getRunningCount();
        if (runningCount > 0) {
            sqlStatMap.put("runningCount", runningCount);
        }

        int concurrentMax = sqlStat.getConcurrentMax();
        if (concurrentMax > 0) {
            sqlStatMap.put("concurrentMax", concurrentMax);
        }

        if (sqlStat.getFetchRowCount() > 0) {
            sqlStatMap.put("fetchRowCount", sqlStat.getFetchRowCount());
            sqlStatMap.put("fetchRowCount", sqlStat.getFetchRowCountMax());
            sqlStatMap.put("fetchRowHistogram", rtrim(sqlStat.getFetchRowHistogram()));
        }

        if (sqlStat.getUpdateCount() > 0) {
            sqlStatMap.put("updateCount", sqlStat.getUpdateCount());
            sqlStatMap.put("updateCountMax", sqlStat.getUpdateCountMax());
            sqlStatMap.put("updateHistogram", rtrim(sqlStat.getUpdateHistogram()));
        }

        if (sqlStat.getInTransactionCount() > 0) {
            sqlStatMap.put("inTransactionCount", sqlStat.getInTransactionCount());
        }

        if (sqlStat.getClobOpenCount() > 0) {
            sqlStatMap.put("clobOpenCount", sqlStat.getClobOpenCount());
        }

        if (sqlStat.getBlobOpenCount() > 0) {
            sqlStatMap.put("blobOpenCount", sqlStat.getBlobOpenCount());
        }
    }

    private void fillGaugeValue(DruidDataSourceStatValue statValue, Map<String, Object> map) {
        map.put("url", statValue.getUrl());
        map.put("dbType", statValue.getDbType());
        map.put("name", statValue.getName());
        map.put("activeCount", statValue.getActiveCount());

        if (statValue.getActivePeak() > 0) {
            map.put("activePeak", statValue.getActivePeak());
            map.put("activePeakTime", statValue.getActivePeakTime());
        }
        map.put("poolingCount", statValue.getPoolingCount());
        if (statValue.getPoolingPeak() > 0) {
            map.put("poolingPeak", statValue.getPoolingPeak());
            map.put("poolingPeakTime", statValue.getPoolingPeakTime());
        }
        map.put("connectCount", statValue.getConnectCount());
        map.put("closeCount", statValue.getCloseCount());

        if (statValue.getWaitThreadCount() > 0) {
            map.put("waitThreadCount", statValue.getWaitThreadCount());
        }

        if (statValue.getNotEmptyWaitCount() > 0) {
            map.put("notEmptyWaitCount", statValue.getNotEmptyWaitCount());
        }

        if (statValue.getNotEmptyWaitMillis() > 0) {
            map.put("notEmptyWaitMillis", statValue.getNotEmptyWaitMillis());
        }

        if (statValue.getLogicConnectErrorCount() > 0) {
            map.put("logicConnectErrorCount", statValue.getLogicConnectErrorCount());
        }

        if (statValue.getPhysicalConnectCount() > 0) {
            map.put("physicalConnectCount", statValue.getPhysicalConnectCount());
        }

        if (statValue.getPhysicalCloseCount() > 0) {
            map.put("physicalCloseCount", statValue.getPhysicalCloseCount());
        }

        if (statValue.getPhysicalConnectErrorCount() > 0) {
            map.put("physicalConnectErrorCount", statValue.getPhysicalConnectErrorCount());
        }

        if (statValue.getExecuteCount() > 0) {
            map.put("executeCount", statValue.getExecuteCount());
        }

        if (statValue.getErrorCount() > 0) {
            map.put("errorCount", statValue.getErrorCount());
        }

        if (statValue.getCommitCount() > 0) {
            map.put("commitCount", statValue.getCommitCount());
        }

        if (statValue.getRollbackCount() > 0) {
            map.put("rollbackCount", statValue.getRollbackCount());
        }

        if (statValue.getPstmtCacheHitCount() > 0) {
            map.put("pstmtCacheHitCount", statValue.getPstmtCacheHitCount());
        }

        if (statValue.getPstmtCacheMissCount() > 0) {
            map.put("pstmtCacheMissCount", statValue.getPstmtCacheMissCount());
        }

        if (statValue.getStartTransactionCount() > 0) {
            map.put("startTransactionCount", statValue.getStartTransactionCount());
            map.put("transactionHistogram", JSON.toJSONString(rtrim(statValue.getTransactionHistogram())));
        }

        if (statValue.getConnectCount() > 0) {
            map.put("connectionHoldTimeHistogram", JSON.toJSONString(rtrim(statValue.getConnectionHoldTimeHistogram())));
        }

        if (statValue.getClobOpenCount() > 0) {
            map.put("clobOpenCount", statValue.getClobOpenCount());
        }

        if (statValue.getBlobOpenCount() > 0) {
            map.put("blobOpenCount", statValue.getBlobOpenCount());
        }

        if (statValue.getSqlSkipCount() > 0) {
            map.put("sqlSkipCount", statValue.getSqlSkipCount());
        }
    }

    private void logPadMap(Map<String, Object> map) {
        if (MapUtils.isNotEmpty(map)) {
            final StringBuilder sb = new StringBuilder();
            final StringBuilder sb2 = new StringBuilder();
            map.forEach((key, value) -> {
                int pad = biggerLength(key, value);
                sb.append(StringUtils.rightPad(key, pad)).append("|");
                sb2.append(StringUtils.rightPad(Objects.toString(value, "null"), pad)).append("|");
            });
            logger().info(sb.toString());
            logger().info(sb2.toString());
        }
    }

    private int biggerLength(String key, Object value) {
        int valueL = StringUtils.length(Objects.toString(value, ""));
        int keyL = StringUtils.length(key);
        if (keyL > valueL)
            return keyL;
        else
            return valueL;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}
