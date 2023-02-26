package com.qdbh.testrabbitmq.a5_return_listener;

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
        channel.exchangeDeclare("测试return", "direct", true, false, null);

        //创建自定义队列
        channel.queueDeclare("return队列", true, false, false, null);

        //绑定交换机与消息队列
        channel.queueBind("return队列", "测试return", "return路由键");

        //添加returnListener监听
        channel.addReturnListener(new ReturnListener() {
            @Override
            public void handleReturn(int replyCode, String replyText, String exchange, String routingKey, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println(replyCode + "\n" + replyText + "\n" + exchange + "\n" + routingKey + "\n" + properties);
            }
        });

        //发送消息
        String a = "测试One";
        //mandatory强制性的 设置消息返还
        channel.basicPublish("测试return", "return路由键错误", true, null, a.getBytes());

        //等会再关闭 不然直接走完了
        Thread.sleep(3000);
        //关闭连接
        channel.close();
        connection.close();
    }
}
