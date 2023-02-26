package com.qdbh.testrabbitmq.a7_custom_consum;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;

/*
    自定义处理类 继承DefaultConsumer 传入channel对象
    重写handleDelivery方法
    使用案例：
        DefaultConsumer consumer = new CommonConsumer(channel);
 */
public class CommonConsumer extends DefaultConsumer {
    /**
     * Constructs a new instance and records its association to the passed-in channel.
     *
     * @param channel the channel to which this consumer is attached
     */
    public CommonConsumer(Channel channel) {
        super(channel);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        super.handleDelivery(consumerTag, envelope, properties, body);
        String s = new String(body);
        System.out.println("接收到消息：" + s);
        //确认收到消息 如果注释掉确认代码就可以清楚的看到只接收到了两条消息 并且队列中的5条消息并不会删除 因为没有确认
        //channel.basicAck(envelope.getDeliveryTag(), false);

        getChannel().basicNack(envelope.getDeliveryTag(), false, false);
    }
}
