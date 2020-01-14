package com.mwb.rpc.impl;

import com.mwb.maf.core.rpc.EnableMotan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by zhangrongbin on 2018/09/27.
 */
@SpringBootApplication
@EnableMotan(namespace = "user")
public class RpcServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RpcServerApplication.class, args);
    }
}
