package com.qdbh.testrabbitmq.a8_ttl;

import com.qdbh.testrabbitmq.util.RabbitMQConnection;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

public class ProducerMessageTTL {

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {

        //抽象
        Connection connection = RabbitMQConnection.getConnection();

        //创建信道
        Channel channel = connection.createChannel();

        //加入交换机
        channel.exchangeDeclare("直连型交换机", "direct", true, false, null);

        //创建自定义队列
        channel.queueDeclare("队列directA", true, false, false, null);

        //绑定交换机与消息队列
        channel.queueBind("队列directA", "直连型交换机", "keyForDirectDemoOne");

        //发送消息
        String a = "十秒过期";
        AMQP.BasicProperties basicProperties = new AMQP.BasicProperties()
                .builder()
                .expiration("10000")
                .build();
        channel.basicPublish("直连型交换机", "keyForDirectDemoOne", true, basicProperties, a.getBytes());

        //等会再关闭 不然直接走完了
        Thread.sleep(3000);
        //关闭连接
        channel.close();
        connection.close();
    }
}
