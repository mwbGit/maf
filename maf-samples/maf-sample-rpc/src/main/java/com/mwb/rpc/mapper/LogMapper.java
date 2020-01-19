package com.mwb.rpc.mapper;

import org.apache.ibatis.annotations.Insert;

public interface LogMapper {

    @Insert("insert into maf_log (trace_id, service_ip, start_time, end_time, process_time, log, type, add_time) " +
            " values (#{traceId},#{serviceIp},#{startTime},#{endTime},#{processTime},#{log},#{type},now())")
    void insert(String traceId, String serviceIp, long startTime, long endTime, long processTime, String log, int type);


}
