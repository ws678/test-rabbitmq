package com.qdbh.testrabbitmq.a9_dead_letter;

import com.qdbh.testrabbitmq.util.RabbitMQConnection;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RefuseConsumer {

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
                System.out.println("接收到消息：" + s + "ok 现在我们拒绝它");
                //拒绝该消息
                channel.basicReject(envelope.getDeliveryTag(), false);
            }
        };

        //模拟拒绝消息场景 将自动确认修改为false
        channel.basicConsume("正常队列", false, consumer);

        //防止直接走完
        Thread.sleep(3000);

        //关闭连接
        channel.close();
        connection.close();
    }
}
