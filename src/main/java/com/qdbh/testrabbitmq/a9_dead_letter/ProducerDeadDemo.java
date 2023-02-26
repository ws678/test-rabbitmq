package com.qdbh.testrabbitmq.a9_dead_letter;

import com.qdbh.testrabbitmq.util.RabbitMQConnection;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class ProducerDeadDemo {

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {

        //抽象
        Connection connection = RabbitMQConnection.getConnection();

        //创建信道
        Channel channel = connection.createChannel();

        //定义死信交换机
        channel.exchangeDeclare("死信交换机A", "topic", true, false, null);

        //加入交换机
        channel.exchangeDeclare("正常交换机", "direct", true, false, null);

        //死信队列
        channel.queueDeclare("死信队列A", true, false, false, null);

        //创建自定义队列 将之与死信交换机绑定
        Map<String, Object> props = new HashMap<>();
        props.put("x-dead-letter-exchange", "死信交换机A");
        channel.queueDeclare("正常队列", true, false, false, props);

        //绑定死信交换机与队列
        channel.queueBind("死信队列A", "死信交换机A", "#.testDead");

        //绑定交换机与消息队列
        channel.queueBind("正常队列", "正常交换机", "testDead");

        String a = "正常队列发送一条存活时间为15秒的信息";
        AMQP.BasicProperties basicProperties = new AMQP.BasicProperties()
                .builder()
                .expiration("15000")
                .build();
        channel.basicPublish("正常交换机", "testDead", true, basicProperties, a.getBytes());

        //等会再关闭 不然直接走完了
        Thread.sleep(3000);
        //关闭连接
        channel.close();
        connection.close();
    }
}
