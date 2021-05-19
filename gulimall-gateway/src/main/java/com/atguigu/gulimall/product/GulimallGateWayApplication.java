package com.atguigu.gulimall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
@MapperScan("com.atguigu.gulimall.product.dao")
public class GulimallGateWayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallGateWayApplication.class, args);
    }

}
