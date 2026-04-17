package com.zentra.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.zentra.server.mapper")
public class ZentraApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZentraApplication.class, args);
    }
}
