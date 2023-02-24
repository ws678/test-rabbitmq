package com.qdbh.testrabbitmq.a2_exchange.fanout;

import com.qdbh.testrabbitmq.util.RabbitMQConnection;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Consumer {

    public static void main(String[] args) throws IOException, TimeoutException {

        //抽象
        Connection connection = RabbitMQConnection.getConnection();

        //创建信道
        Channel channel = connection.createChannel();

        //接收数据
        DefaultConsumer defaultConsumer = new DefaultConsumer(channel){
            //重写处理逻辑
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                super.handleDelivery(consumerTag, envelope, properties, body);

                String result = new String(body);
                System.out.println(result);
            }
        };
        channel.basicConsume("队列fanoutA", true, defaultConsumer);
        channel.basicConsume("队列fanoutB", true, defaultConsumer);

        //关闭连接
        channel.close();
        connection.close();
    }
}
