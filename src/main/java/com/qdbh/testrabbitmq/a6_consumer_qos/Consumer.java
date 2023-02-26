package com.qdbh.testrabbitmq.a6_consumer_qos;

import com.qdbh.testrabbitmq.util.RabbitMQConnection;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Consumer {

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {

        //抽象
        Connection connection = RabbitMQConnection.getConnection();

        //创建信道
        Channel channel = connection.createChannel();

        //设置qos 每次要接收多少条消息
        channel.basicQos(2);

        //接收数据
        DefaultConsumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                super.handleDelivery(consumerTag, envelope, properties, body);
                String s = new String(body);
                System.out.println("接收到消息：" + s);
                //确认收到消息 如果注释掉确认代码就可以清楚的看到只接收到了两条消息 并且队列中的5条消息并不会删除 因为没有确认
                //channel.basicAck(envelope.getDeliveryTag(), false);

                //channel.basicNack(envelope.getDeliveryTag(), false, false); 也可以使用Nack 最后一个参数设置为false 消息一旦发送 不需要确认即丢失
            }
        };

        //将autoAck设置为不自动签收
        channel.basicConsume("qos队列", false, consumer);

        //防止直接走完
        Thread.sleep(3000);

        //关闭连接
        channel.close();
        connection.close();
    }
}
