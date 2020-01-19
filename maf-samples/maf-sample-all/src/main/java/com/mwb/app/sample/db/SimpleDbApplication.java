package com.mwb.app.sample.db;

import com.mwb.maf.core.db.EnableDataSource;
import com.mwb.maf.core.kv.EnableJedisClient;
import com.mwb.maf.core.kv.EnableJedisClusterClient;
import com.mwb.maf.core.logging.EnableTraceLog;
import com.mwb.maf.core.rpc.EnableMotan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
@EnableTraceLog(namespace = "log", write = true)
@EnableMotan(namespace = "user")
@EnableMotan(namespace = "shop")
@EnableJedisClient
@EnableJedisClusterClient(namespace = "mwb")
@EnableDataSource(mapperPackages = "com.mwb.app.sample.db.mapper")
public class SimpleDbApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleDbApplication.class, args);
    }
}
