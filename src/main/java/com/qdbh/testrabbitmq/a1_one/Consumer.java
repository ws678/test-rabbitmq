package com.qdbh.testrabbitmq.a1_one;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class Consumer {

    public static void main(String[] args) throws IOException, TimeoutException {

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");

        //创建连接
        Connection connection = connectionFactory.newConnection();

        //创建信道
        Channel channel = connection.createChannel();

        //接收数据
        DefaultConsumer defaultConsumer = new DefaultConsumer(channel){
            //重写处理逻辑
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                super.handleDelivery(consumerTag, envelope, properties, body);

                /*String result = new String(body);*/
                //获取自定义详情
                Map<String, Object> headers = properties.getHeaders();
                if (headers != null){
                    headers.forEach((k,v) ->
                                    System.out.println("收到消息：" + k + "\n内容为：" + v + "\n")
                            );
                }
            }
        };
        channel.basicConsume("PHONE-queue-PERSISTENT", true, defaultConsumer);

        //关闭连接
        channel.close();
        connection.close();
    }
}
