package com.qdbh.testrabbitmq.util;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Author wangshuo
 * @Date 2022/5/6, 16:51
 * Please add a comment
 */
public class RabbitMQConnection {

    //获取连接
    public static Connection getConnection() throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        //指明VirtualHost
        //factory.setVirtualHost("/MuPing");
        //设置账号和密码
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        return factory.newConnection();
    }
}
