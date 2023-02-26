package com.qdbh.testrabbitmq.a9_dead_letter;

import com.qdbh.testrabbitmq.util.RabbitMQConnection;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ConsumerDeadDemo {

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {

        //抽象
        Connection connection = RabbitMQConnection.getConnection();

        //创建信道
        Channel channel = connection.createChannel();

        //接收数据
        DefaultConsumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                super.handleDelivery(consumerTag, envelope, properties, body);
                String s = new String(body);
                System.out.println("接收到消息：" + s);
            }
        };

        //channel.basicConsume("正常队列", true, consumer);
        //接收死信队列的消息
        channel.basicConsume("死信队列A", true, consumer);

        //防止直接走完
        Thread.sleep(3000);

        //关闭连接
        channel.close();
        connection.close();
    }
}
