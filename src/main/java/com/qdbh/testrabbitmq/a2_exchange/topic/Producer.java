package com.qdbh.testrabbitmq.a2_exchange.topic;

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
        channel.exchangeDeclare("topic型交换机", "topic", true, false, null);

        //声明队列
        channel.queueDeclare("队列topicA", true, false, false, null);
        channel.queueDeclare("队列topicB", true, false, false, null);
        channel.queueDeclare("队列topicC", true, false, false, null);

        //绑定交换机与队列
        channel.queueBind("队列topicB", "topic型交换机", "*.代表一个字符");
        channel.queueBind("队列topicC", "topic型交换机", "*.*.代表一个字符");
        channel.queueBind("队列topicA", "topic型交换机", "#.代表一个或者多个字符");

        //发送数据
        String demoStr = "topic交换机发送数据：";
        channel.basicPublish("topic型交换机", "queue.代表一个字符", null, demoStr.concat("啦啦啦代表一个字符").getBytes());
        channel.basicPublish("topic型交换机", ".哎呦你干嘛两个单词.试试两个星号", null, demoStr.concat("哎呦你干嘛.哈啊哈.啦拉拉").getBytes());
        channel.basicPublish("topic型交换机", "母鸡母鸡.哈啊哈.啦拉拉.代表一个或者多个字符", null, demoStr.concat("代表一个或者多个字符").getBytes());

        //关闭连接
        channel.close();
        connection.close();
    }
}
