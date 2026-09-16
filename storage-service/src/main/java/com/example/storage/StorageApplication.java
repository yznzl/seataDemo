package com.example.storage;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/** 库存服务启动类。 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.example.storage.mapper")
public class StorageApplication {
    /**
     * 启动库存服务。
     *
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(StorageApplication.class, args);
    }
}
