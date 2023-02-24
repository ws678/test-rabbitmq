package com.qdbh.testrabbitmq.a2_exchange.headers;

import com.qdbh.testrabbitmq.util.RabbitMQConnection;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class Producer {

    public static void main(String[] args) throws IOException, TimeoutException {

        //抽象
        Connection connection = RabbitMQConnection.getConnection();

        //创建信道
        Channel channel = connection.createChannel();

        //创建交换机
        //匹配all
        channel.exchangeDeclare("headers交换机", "headers", true, false, null);

        //声明队列
        channel.queueDeclare("队列headersAll", true, false, false, null);
        channel.queueDeclare("队列headersAny", true, false, false, null);

        //定义keyMap

        Map<String, Object> keyMapForAll = new HashMap<>();
        keyMapForAll.put("x-match", "all");
        keyMapForAll.put("name", "随便写");
        keyMapForAll.put("gender", "男");
        keyMapForAll.put("age", "888");

        Map<String, Object> keyMapForAny = new HashMap<>();
        keyMapForAny.put("name", "随便写");
        keyMapForAny.put("gender", "只匹配一个就行");
        keyMapForAny.put("age", "我随便写");
        keyMapForAny.put("x-match", "any");

        //绑定交换机与队列
        channel.queueBind("队列headersAll", "headers交换机", "*.代表一个字符", keyMapForAll);
        channel.queueBind("队列headersAny", "headers交换机", "*.*.代表一个字符", keyMapForAny);

        //发送的额外数据不需要携带x-match
        keyMapForAll.remove("x-match");
        keyMapForAny.remove("x-match");
        AMQP.BasicProperties propsForAll = getBasicProperties(keyMapForAll);
        AMQP.BasicProperties propsForAny = getBasicProperties(keyMapForAny);
        //发送数据
        String demoStr = "headers交换机发送数据：";
        channel.basicPublish("headers交换机", "随便写", propsForAll, demoStr.concat("matchAll").getBytes());
        channel.basicPublish("headers交换机", ".哎呦你干嘛两个单词.试试两个星号", propsForAny, demoStr.concat("matchAny").getBytes());

        //关闭连接
        channel.close();
        connection.close();
    }

    //抽象
    private static AMQP.BasicProperties getBasicProperties(Map<String, Object> keyMap) {
        AMQP.BasicProperties props = new AMQP.BasicProperties()
                .builder()
                .contentEncoding("UTF-8")
                .deliveryMode(2)
                .headers(keyMap)
                .build();
        return props;
    }
}
