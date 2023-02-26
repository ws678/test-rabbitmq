package com.qdbh.testrabbitmq.a4_confirm_listener;

import com.qdbh.testrabbitmq.util.RabbitMQConnection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

public class Producer {

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {

        //抽象
        Connection connection = RabbitMQConnection.getConnection();

        //创建信道
        Channel channel = connection.createChannel();

        //创建自定义队列
        channel.queueDeclare("确认消息是否收到", true, false, false, null);

        //打开确认模式
        channel.confirmSelect();
        //添加监听
        channel.addConfirmListener((deliveryTag, multiple) -> {

            System.out.println("消息已成功到达MQ");
        }, (deliveryTag, multiple) -> {

            System.out.println("error：消息未成功到达");
        });

        //发送消息
        String a = "测试One";
        channel.basicPublish("", "确认消息是否收到", null, a.getBytes());

        //等会再关闭 不然直接走完了
        Thread.sleep(3000);
        //关闭连接
        channel.close();
        connection.close();
    }
}
