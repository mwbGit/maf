package com.mwb.maf.core.logging.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

public interface MafTraceLogMapper {

    @Insert("insert into maf_log (trace_id, service_ip, start_time, end_time, process_time, log, type, add_time) " +
            " values (#{traceId},#{serviceIp},#{startTime},#{endTime},#{processTime},#{log},#{type},now())")
    void insert(@Param("traceId") String traceId, @Param("serviceIp") String serviceIp, @Param("startTime") long startTime, @Param("endTime") long endTime,
                @Param("processTime") long processTime, @Param("log") String log, @Param("type") int type);


}
