package com.qdbh.testrabbitmq.a8_ttl;

import com.qdbh.testrabbitmq.util.RabbitMQConnection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

public class ProducerQueueTTL {

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {

        //抽象
        Connection connection = RabbitMQConnection.getConnection();

        //创建信道
        Channel channel = connection.createChannel();

        //加入交换机
        channel.exchangeDeclare("交换机TTL60秒", "direct", true, false, null);

        //新建props 设置ttl
        HashMap<String, Object> props = new HashMap<>();
        props.put("x-message-ttl", 60000);

        //创建自定义队列
        channel.queueDeclare("队列TTL60秒", true, false, false, props);

        //绑定交换机与消息队列
        channel.queueBind("队列TTL60秒", "交换机TTL60秒", "ttlTest");

        //发送消息
        String a = "这条消息多久过期捏";
        channel.basicPublish("交换机TTL60秒", "ttlTest", true, null, a.getBytes());

        //等会再关闭 不然直接走完了
        Thread.sleep(3000);
        //关闭连接
        channel.close();
        connection.close();
    }
}
