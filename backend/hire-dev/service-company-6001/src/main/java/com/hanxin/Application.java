package com.hanxin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient  // 开启注册中心的服务注册和发现功能
@MapperScan(basePackages = "com.hanxin.mapper")
@EnableFeignClients("com.hanxin.api.feign")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
