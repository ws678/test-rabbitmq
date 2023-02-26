package com.qdbh.testrabbitmq.a6_consumer_qos;

import com.qdbh.testrabbitmq.util.RabbitMQConnection;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ReturnListener;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Producer {

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {

        //抽象
        Connection connection = RabbitMQConnection.getConnection();

        //创建信道
        Channel channel = connection.createChannel();

        //加入交换机
        channel.exchangeDeclare("qos交换机", "direct", true, false, null);

        //创建自定义队列
        channel.queueDeclare("qos队列", true, false, false, null);

        //绑定交换机与消息队列
        channel.queueBind("qos队列", "qos交换机", "qos");

        //发送消息
        for (int i = 0; i < 5; i++) {

            String a = "发送第" + i + "条信息";
            channel.basicPublish("qos交换机", "qos", true, null, a.getBytes());
        }

        //等会再关闭 不然直接走完了
        Thread.sleep(3000);
        //关闭连接
        channel.close();
        connection.close();
    }
}