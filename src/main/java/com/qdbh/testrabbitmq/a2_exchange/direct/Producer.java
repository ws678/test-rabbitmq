package com.qdbh.testrabbitmq.a2_exchange.direct;

import com.qdbh.testrabbitmq.util.RabbitMQConnection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Producer {

    public static void main(String[] args) throws IOException, TimeoutException {

        //抽象
        Connection connection = RabbitMQConnection.getConnection();

        //创建信道
        Channel channel = connection.createChannel();

        //创建交换机
        channel.exchangeDeclare("direct_exchange", "direct", true, false, null);

        //声明队列
        channel.queueDeclare("queueC", true, false, false, null);
        channel.queueDeclare("queueD", true, false, false, null);

        //绑定交换机与队列
        channel.queueBind("queueC", "direct_exchange", "email");
        channel.queueBind("queueD", "direct_exchange", "sms");

        //发送数据
        String demoStr = "直连型交换机向DemoOne发送数据";
        channel.basicPublish("direct_exchange", "email", null, demoStr.getBytes());

        //关闭连接
        channel.close();
        connection.close();
    }
}
