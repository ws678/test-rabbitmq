package com.qdbh.testrabbitmq.a7_custom_consum;

import com.qdbh.testrabbitmq.util.RabbitMQConnection;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class CustomConsumClass {

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {

        //抽象
        Connection connection = RabbitMQConnection.getConnection();

        //创建信道
        Channel channel = connection.createChannel();

        //设置qos 每次要接收多少条消息
        channel.basicQos(2);

        //使用自定义的处理类返回DefaultConsumer
        DefaultConsumer consumer = new CommonConsumer(channel);

        //将autoAck设置为不自动签收
        channel.basicConsume("qos队列", false, consumer);

        //防止直接走完
        Thread.sleep(3000);

        //关闭连接
        channel.close();
        connection.close();
    }
}
