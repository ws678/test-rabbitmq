package com.qdbh.testrabbitmq.a1_one;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

public class Producer {

    /*
        连接：一次请求
        信道：单次连接可以创建多个信道
        消息队列：同一请求内 -- 每个信道可以连接多个消息队列 每个消息队列可以同时被多个信道连接
     */
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

        //创建消息队列
        //参数含义 1、队列名字 2、rabbitmq重启之后消息队列是否要继续存在 3、队列是否要设置为独占 4、队列没有连接与数据时是否自动删除 5、额外的自定义参数
        channel.queueDeclare("PHONE-queue", true, false, false, null);
        channel.queueDeclare("PHONE-queue-PERSISTENT", true, false, false, null);

        //发送数据
        //参数含义 1、交换机 2、队列名 3、附加信息 4、要发送的信息
        String demoStr = "Test";
        /*channel.basicPublish("", "PHONE-queue", null, demoStr.getBytes());

        //MessageProperties.PERSISTENT_TEXT_PLAIN 设置该消息写入磁盘进行持久化
        channel.basicPublish("", "PHONE-queue-PERSISTENT", MessageProperties.PERSISTENT_TEXT_PLAIN, demoStr.getBytes());*/

        //详细的自定义
        HashMap<String, Object> headers = new HashMap<>();
        headers.put("姓名","为啥");
        headers.put("phone","123");
        headers.put("address","翻斗花园");
        AMQP.BasicProperties basicProperties = new AMQP.BasicProperties()
                .builder()
                .contentEncoding("UTF-8")
                //.expiration("10000") //生存十秒
                .deliveryMode(2) //1、不进行持久化 2、持久化到磁盘
                .headers(headers) //头部信息 自定义的属性
                .build();
        channel.basicPublish("", "PHONE-queue-PERSISTENT", basicProperties, demoStr.getBytes());

        //关闭连接
        channel.close();
        connection.close();
    }
}
