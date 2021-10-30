package com.atguigu.gulimall.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 1、引入amqp
 * 2、容器中自动配置了
 * RabbitTemplate  AmqpAdmin  RabbitMessagingTemplate CacheConnectionFactory
 * 3、@EnableRabbit
 * 4、配置链接信息
 * 5、监听消息
 *   @RabbitListener 标类上 方法上  监听队列
 *   @RabbitHandler标方法上  重载区分不同消息
 * spring.rabbitmq.publisher-confirms=true
 * spring.rabbitmq.publisher-returns=true
 */

@EnableRedisHttpSession
@EnableRabbit
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
@MapperScan("com.atguigu.gulimall.order.dao")
public class GulimallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallOrderApplication.class, args);
    }



}
