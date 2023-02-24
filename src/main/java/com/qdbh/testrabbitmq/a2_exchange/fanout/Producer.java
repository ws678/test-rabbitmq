package com.qdbh.testrabbitmq.a2_exchange.fanout;

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
        channel.exchangeDeclare("fanout型交换机", "fanout", true, false, null);

        //声明队列
        channel.queueDeclare("队列fanoutA", true, false, false, null);
        channel.queueDeclare("队列fanoutB", true, false, false, null);

        //绑定交换机与队列
        channel.queueBind("队列fanoutA", "fanout型交换机", "fanout没有routeingKey可以瞎写或不填");
        channel.queueBind("队列fanoutB", "fanout型交换机", "这个瞎写");

        //发送数据
        String demoStr = "fanout交换机直接发送数据";
        channel.basicPublish("fanout型交换机", "keyForDirectDemoOne", null, demoStr.getBytes());

        //关闭连接
        channel.close();
        connection.close();
    }
}
