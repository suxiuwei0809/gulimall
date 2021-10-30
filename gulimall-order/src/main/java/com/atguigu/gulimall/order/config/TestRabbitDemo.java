//package com.atguigu.gulimall.order.config;
//
//import com.atguigu.gulimall.order.entity.OrderReturnApplyEntity;
//import com.atguigu.gulimall.order.entity.OrderReturnReasonEntity;
//import com.rabbitmq.client.Channel;
//import org.springframework.amqp.core.*;
//import org.springframework.amqp.rabbit.annotation.RabbitHandler;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.amqp.rabbit.connection.CorrelationData;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.util.UUID;
//
//@Component
//public class TestRabbitDemo {
//    @Autowired
//    AmqpAdmin amqpAdmin;
//    @Autowired
//    RabbitTemplate rabbitTemplate;
//
//    // 创建链接
//    public void RabbitTest(){
//        //创建交换机
//        DirectExchange directExchange = new DirectExchange("hello-exchange",true,false);
//        amqpAdmin.declareExchange(directExchange);
//        //创建队列
//        Queue queue = new Queue("hello-queue",true,false,false);
//        amqpAdmin.declareQueue(queue);
//        //绑定交换机 和 队列之间的关系
//        Binding binding = new Binding("hello-queue",
//                Binding.DestinationType.QUEUE,"hello-exchange","hello",null);
//        amqpAdmin.declareBinding(binding);
//    }
//
//    //发送消息
//    public void sendMessageTest(){
//        //1、发送消息
//        rabbitTemplate.convertAndSend("hello-exchange","hello","hello word!",new CorrelationData(UUID.randomUUID().toString()));
//    }
//
//    //接收
//    //Message  原生  消息头 消息体
//    //Channel channel 当前传输数据的通道
//    @RabbitListener(queues={"hello-queue"})
//    public void RabbitListener(Message message,
//                               OrderReturnReasonEntity content,
//                               Channel channel){
//        System.out.println("监听到的消息：  "+message.getBody());
//        System.out.println("监听到的消息内容   "+content);
//        long deliveryTag = message.getMessageProperties().getDeliveryTag();
//        //deliveryTag   Channel 内按顺序自增
//        //手动ack消息
//        //spring.rabbitmq.listener.simple.acknowledge-mode=manual
//        //消息签收非批量模式
//        try {
//            if(deliveryTag%2==0){
//                /**
//                 * deliveryTag 队列中消息的为之id
//                 * false 是否批量签收
//                 */
//                channel.basicAck(deliveryTag,false);
//            }else {
//                /**
//                 * deliveryTag 队列中消息的为之id
//                 * false 是否批量拒绝签收
//                 * true 是否丢弃
//                 */
//                channel.basicNack(deliveryTag,false,false);
//            }
//        } catch (IOException e) {
//            //网络中断
//            e.printStackTrace();
//        }
//    }
//    //注解标注 重载  监听不同消息类型
//    @RabbitHandler
//    public void RabbitListener2(Message message, OrderReturnReasonEntity content, Channel channel){
//        System.out.println("监听到的消息内容   "+content);
//    }
//
//    @RabbitHandler
//    public void RabbitListener2(Message message, OrderReturnApplyEntity content, Channel channel){
//        System.out.println("监听到的消息内容   "+content);
//    }
//}
