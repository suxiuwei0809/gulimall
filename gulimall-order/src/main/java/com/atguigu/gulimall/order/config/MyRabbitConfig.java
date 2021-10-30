package com.atguigu.gulimall.order.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.annotation.PostConstruct;


@Configuration
public class MyRabbitConfig {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Bean // 1、配置消息传输对象为json
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    //定制template对象
    @PostConstruct//该注解 MyRabbitConfig对象创建完以后，执行这个方法
    public void InitTemplate() {
        //设置消息到broker确认回调
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             * spring.rabbitmq.publisher-confirms=true  开启发送到broker确认
             *
             * @param correlationData 当前消息的唯一关联数据（这个消息是唯一ID）
             * @param b  消息是否成功收到 ,确认消息发送到broker,为true
             * @param cause 失败的原因
             */
            @Override
            public void confirm(CorrelationData correlationData, boolean b, String cause) {
                System.out.println("当前消息的唯一关联数据：" + correlationData.getId());
                System.out.println("当前消息的唯一关联数据：" + correlationData.getFuture());
                System.out.println("当前消息的唯一关联数据：" + correlationData.getReturnedMessage());
                System.out.println("消息是否成功收到：" + b);
                System.out.println("失败的原因：" + cause);
            }
        });
        //设置消息没有到Queue确认回调
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            /**
             *只要消息没有投递给指定的队列,就触发这个失败的回调
             * @param message
             * @param replyCode
             * @param replyText
             * @param exchange
             * @param routKey
             */
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routKey) {
                System.out.println("失败的消息"+message);
                System.out.println("失败码"+replyCode);
                System.out.println("失败码说明"+replyText);
                System.out.println("交换机"+exchange);
                System.out.println("路由键"+routKey);
            }
        });

        /**
         * 消费端确认(保证每个消息被正确消费，此时broker删除这个消息)
         * 默认是自动确认的，只要消息接收到，客户端会自动确认，服务端就会移除个消息
         * 问题：我们收到很多消息自动回复给服务器ack，只有一个消息处理成功后，宕机了，后续的消息 会出现消息丢失。
         * 解决：手动确认模式：只要我们没有明确告诉mq，消息被签收（ack），没有ack，消息就一直是unacked状态。
         *      即使consumer宕机不会丢失，会重新变为ready，下一次有consumer链接就发给他
         */
    }
}
