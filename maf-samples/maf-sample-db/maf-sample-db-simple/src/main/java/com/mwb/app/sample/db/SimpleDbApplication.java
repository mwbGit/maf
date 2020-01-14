package com.mwb.app.sample.db;

import com.mwb.maf.core.db.EnableDataSource;
import com.mwb.maf.core.kv.EnableJedisClient;
import com.mwb.maf.core.kv.EnableJedisClusterClient;
import com.mwb.maf.core.rpc.EnableMotan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableMotan(namespace = "user")
@EnableJedisClient
@EnableJedisClusterClient(namespace = "mwb")
@EnableDataSource(mapperPackages = "com.mwb.app.sample.db.mapper")
public class SimpleDbApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleDbApplication.class, args);
    }
}
