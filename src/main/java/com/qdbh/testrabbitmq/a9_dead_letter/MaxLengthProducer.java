package com.qdbh.testrabbitmq.a9_dead_letter;

import com.qdbh.testrabbitmq.util.RabbitMQConnection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

public class MaxLengthProducer {

    public static void main(String[] args) throws IOException, TimeoutException {

        //抽象
        Connection connection = RabbitMQConnection.getConnection();

        //创建信道
        Channel channel = connection.createChannel();

        //props
        HashMap<String, Object> props = new HashMap<>();
        props.put("x-max-length", 2); //最多保存两个消息
        props.put("x-dead-letter-exchange", "死信交换机A"); //之前的代码创建过交换机了 直接沿用

        //创建自定义队列
        channel.queueDeclare("新队列.消息死了", true, false, false, props);

        //发送消息
        java.lang.String a = "测试One";
        java.lang.String b = "测试Two";
        java.lang.String c = "测试Three";
        channel.basicPublish("", "新队列.消息死了", null, a.getBytes());
        channel.basicPublish("", "新队列.消息死了", null, b.getBytes());
        channel.basicPublish("", "新队列.消息死了", null, c.getBytes());

        /*
            经过实测 发送多条消息不会报错 但是只会保存最新的两条
            new : 现在加入了死信机制 多出的消息会进入死信队列
         */

        //关闭连接
        channel.close();
        connection.close();
    }
}