package com.qdbh.testrabbitmq.a3_max_langth_queue;

import com.qdbh.testrabbitmq.util.RabbitMQConnection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

public class Producer {

    public static void main(String[] args) throws IOException, TimeoutException {

        //抽象
        Connection connection = RabbitMQConnection.getConnection();

        //创建信道
        Channel channel = connection.createChannel();

        //props
        HashMap<String, Object> props = new HashMap<>();
        props.put("x-max-length", 2); //最多保存两个消息

        //创建自定义队列
        channel.queueDeclare("Test队列大小限制", true, false, false, props);

        //发送消息
        java.lang.String a = "测试One";
        java.lang.String b = "测试Two";
        java.lang.String c = "测试Three";
        channel.basicPublish("", "Test队列大小限制", null, a.getBytes());
        channel.basicPublish("", "Test队列大小限制", null, b.getBytes());
        channel.basicPublish("", "Test队列大小限制", null, c.getBytes());

        /*
            经过实测 发送多条消息不会报错 但是只会保存最新的两条
         */

        //关闭连接
        channel.close();
        connection.close();
    }
}
