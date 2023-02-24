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
        channel.exchangeDeclare("直连型交换机", "direct", true, false, null);

        //声明队列
        channel.queueDeclare("队列directA", true, false, false, null);
        channel.queueDeclare("队列directB", true, false, false, null);

        //绑定交换机与队列
        channel.queueBind("队列directA", "直连型交换机", "keyForDirectDemoOne");
        channel.queueBind("队列directB", "直连型交换机", "keyForDirectDemoTwo");

        //发送数据
        String demoStr = "直连型交换机向DemoOne发送数据";
        channel.basicPublish("直连型交换机", "keyForDirectDemoOne", null, demoStr.getBytes());

        //关闭连接
        channel.close();
        connection.close();
    }
}
