package com.qdbh.testrabbitmq.controller;

import com.qdbh.testrabbitmq.a91_spring_listener_adapter.MyOrder;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class TestConsumer {

    //设置监听哪个队列
    @RabbitListener(
            queues = {"queueD"}
    )
    @RabbitHandler
    public void onMessage(Message msg, Channel channel) throws IOException {

        String s = new String((byte[]) msg.getPayload());
        System.out.println("收到了：" + s);
        // msg.getHeaders().forEach();
        Long delivery = (Long) msg.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
        //手动确认消息
        channel.basicAck(delivery, false);
    }

    //设置监听哪个队列
    @RabbitListener(
            queues = {"queueC"}
    )
    @RabbitHandler
    public void onMessageQueueC(@Payload MyOrder myOrder, Channel channel, @Headers Map<String, Object> map) throws IOException {//不要忘记加注解

        System.out.println("收到了：\n" + myOrder.toString());
        // map.getHeaders().forEach();
        Long delivery = (Long) map.get(AmqpHeaders.DELIVERY_TAG);
        //手动确认消息
        channel.basicAck(delivery, false);
    }

    //使用复合注解创建队列
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "注解queue", durable = "true", exclusive = "false", autoDelete = "false"),
                    exchange = @Exchange(value = "注解exchange", type = "direct", durable = "true", autoDelete = "false"),
                    key = "spring注解"
            )
    )
    @RabbitHandler
    public void onMessageNewQueue(Message msg, Channel channel) throws IOException {

        String s = new String((byte[]) msg.getPayload());
        System.out.println("收到了：" + s);
        // msg.getHeaders().forEach();
        Long delivery = (Long) msg.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
        if (null != delivery)
            //手动确认消息
            channel.basicAck(delivery, false);
    }
}
