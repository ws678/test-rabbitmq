package com.qdbh.testrabbitmq.controller;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
/*
    没吊用
 */
@Component
public class TestController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void commonSend(String exchangeName, Message message, String routingKey, CorrelationData correlationData) {

        //设置回调方法
        //rabbitTemplate.setReturnsCallback(returnsCallback);
        rabbitTemplate.setConfirmCallback(confirmCallback);
        rabbitTemplate.convertAndSend(exchangeName, routingKey, message, correlationData);
    }

    RabbitTemplate.ReturnsCallback returnsCallback = new RabbitTemplate.ReturnsCallback() {
        @Override
        public void returnedMessage(ReturnedMessage returned) {
            //输出一下对象
            System.out.println("输出一下" + returned.toString());
        }
    };

    RabbitTemplate.ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback() {
        @Override
        public void confirm(CorrelationData correlationData, boolean ack, String cause) {
            if (ack) {
                System.out.println("发送失败 将correlationData和cause存入数据库");
            } else {
                System.out.println("消息发送成功");
                //……
            }
        }
    };
}
