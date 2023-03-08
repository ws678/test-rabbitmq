package com.qdbh.testrabbitmq.a91_spring_listener_adapter;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

import java.util.Map;

//自定义转换类 实现MessageConverter
public class MyMsgConverter implements MessageConverter {

    //java对象转换为message对象
    @Override
    public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {

        return new Message(object.toString().getBytes(), messageProperties);
    }

    //message对象转换为Java对象
    @Override
    public Object fromMessage(Message message) throws MessageConversionException {

        //想要什么都可以从messageProperties 或者 body中取到
        MessageProperties messageProperties = message.getMessageProperties();
        Map<String, Object> headers = messageProperties.getHeaders();
        //String str = new String(message.getBody());
        return headers;
    }
}
