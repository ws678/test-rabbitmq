package com.qdbh.testrabbitmq;

import com.qdbh.testrabbitmq.a91_spring_listener_adapter.MyMessageDelegate;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.ConsumerTagStrategy;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({"com.qdbh.testrabbitmq.*"})
public class RabbitMQConfiguration {
    //工厂
    @Bean
    public ConnectionFactory connectionFactory() {

        //使用spring定义的实现类接收新建的工厂
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses("localhost:5672");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        return connectionFactory;
    }

    //rabbitAdmin
    @Bean
    public RabbitAdmin rabbitAdmin() {

        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory());
        return rabbitAdmin;
    }

    //rabbitTemplate
    /*
    @Bean
    public RabbitTemplate rabbitTemplate() {

        return new RabbitTemplate(connectionFactory());
    }*/

    @Bean
    Queue queueC() {
        return new Queue("queueC", true, false, false, null);
    }

    @Bean
    Queue queueD() {
        return new Queue("queueD", true, false, false, null);
    }

    //回调方法
    //@Bean
    RabbitTemplate.ReturnsCallback myReturnsCallback() {
        RabbitTemplate.ReturnsCallback returnsCallback = new RabbitTemplate.ReturnsCallback() {
            @Override
            public void returnedMessage(ReturnedMessage returned) {
                //输出一下对象
                System.out.println("输出一下" + returned.toString());
            }
        };
        return returnsCallback;
    }

    //@Bean
    RabbitTemplate.ConfirmCallback myConfirmCallback() {
        return new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                if (ack) {
                    System.out.println("消息发送成功");
                } else {
                    System.out.println("发送失败 将correlationData和cause存入数据库");
                    //……
                }
            }
        };
    }

    //simpleMessageListenerContainer 消息监听容器
    /*@Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer() {
        SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer(connectionFactory());
        simpleMessageListenerContainer.setQueues(queueC(), queueD());
        //一个消费者对象可以监听几个队列
        simpleMessageListenerContainer.setConcurrentConsumers(1);
        //最多创建几个消费者对象
        simpleMessageListenerContainer.setMaxConcurrentConsumers(5);
        //先设置自动确认
        simpleMessageListenerContainer.setAcknowledgeMode(AcknowledgeMode.AUTO);
        //创建消费者标签
        simpleMessageListenerContainer.setConsumerTagStrategy(new ConsumerTagStrategy() {
            @Override
            public String createConsumerTag(String queue) {
                return queue;
            }
        });
        //收到消息后进行消费
        simpleMessageListenerContainer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                String msg = new String(message.getBody());
                System.out.println(msg);
                Map<String, Object> headers = message.getMessageProperties().getHeaders();
                headers.forEach((k, v) -> {
                    System.out.println("map遍历---\nkey:" + k + "\nvalue:" + v);
                });
            }
        });
        return simpleMessageListenerContainer;
    }*/

    //手动确认消息
    /*@Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer() {
        SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer(connectionFactory());
        simpleMessageListenerContainer.setQueues(queueC(), queueD());
        //一个消费者对象可以监听几个队列
        simpleMessageListenerContainer.setConcurrentConsumers(1);
        //最多创建几个消费者对象
        simpleMessageListenerContainer.setMaxConcurrentConsumers(5);
        //设置手动确认
        simpleMessageListenerContainer.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        //创建消费者标签
        simpleMessageListenerContainer.setConsumerTagStrategy(new ConsumerTagStrategy() {
            @Override
            public String createConsumerTag(String queue) {
                return queue;
            }
        });
        //收到消息后进行消费
        simpleMessageListenerContainer.setMessageListener(new ChannelAwareMessageListener() {
            @Override
            public void onMessage(Message message, Channel channel) throws Exception {
                String msg = new String(message.getBody());
                System.out.println("msg:" + msg);
                Map<String, Object> headers = message.getMessageProperties().getHeaders();
                headers.forEach((k, v) -> {
                    System.out.println("map遍历---\nkey:" + k + "\nvalue:" + v);
                });
                //对消息进行手动确认
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                //退货处理
                //channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            }
        });
        return simpleMessageListenerContainer;
    }*/

    //使用监听适配器进行消息的接收
    /*@Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer() {
        SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer(connectionFactory());
        simpleMessageListenerContainer.setQueues(queueC(), queueD());
        //一个消费者对象可以监听几个队列
        simpleMessageListenerContainer.setConcurrentConsumers(1);
        //最多创建几个消费者对象
        simpleMessageListenerContainer.setMaxConcurrentConsumers(5);
        //设置手动确认
        simpleMessageListenerContainer.setAcknowledgeMode(AcknowledgeMode.AUTO);
        //创建消费者标签
        simpleMessageListenerContainer.setConsumerTagStrategy(new ConsumerTagStrategy() {
            @Override
            public String createConsumerTag(String queue) {
                return queue;
            }
        });
        //监听适配器的定义
        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter();
        messageListenerAdapter.setDelegate(new MyMessageDelegate());
        //指定自定义的处理方法
        messageListenerAdapter.setDefaultListenerMethod("myHandleMessage");
        //指定适配器
        simpleMessageListenerContainer.setMessageListener(messageListenerAdapter);
        return simpleMessageListenerContainer;
    }*/

    //来自不同队列的消息使用不同的方法进行处理
    /*@Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer() {
        SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer(connectionFactory());
        simpleMessageListenerContainer.setQueues(queueC(), queueD());
        //一个消费者对象可以监听几个队列
        simpleMessageListenerContainer.setConcurrentConsumers(1);
        //最多创建几个消费者对象
        simpleMessageListenerContainer.setMaxConcurrentConsumers(5);
        //设置手动确认
        simpleMessageListenerContainer.setAcknowledgeMode(AcknowledgeMode.AUTO);
        //创建消费者标签
        simpleMessageListenerContainer.setConsumerTagStrategy(new ConsumerTagStrategy() {
            @Override
            public String createConsumerTag(String queue) {
                return queue;
            }
        });
        //监听适配器的定义
        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter();
        messageListenerAdapter.setDelegate(new MyMessageDelegate());
        //指定Map 不同队列使用不同的处理方法
        Map<String,String> queueMap = new HashMap<>();
        queueMap.put("queueC", "queueCHandleMessage");
        queueMap.put("queueD", "queueDHandleMessage");
        messageListenerAdapter.setQueueOrTagToMethodName(queueMap);
        //指定适配器
        simpleMessageListenerContainer.setMessageListener(messageListenerAdapter);
        return simpleMessageListenerContainer;
    }*/

    //使用自带的json转换类
    /*@Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer() {
        SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer(connectionFactory());
        simpleMessageListenerContainer.setQueues(queueC(), queueD());
        //一个消费者对象可以监听几个队列
        simpleMessageListenerContainer.setConcurrentConsumers(1);
        //最多创建几个消费者对象
        simpleMessageListenerContainer.setMaxConcurrentConsumers(5);
        //设置手动确认
        simpleMessageListenerContainer.setAcknowledgeMode(AcknowledgeMode.AUTO);
        //创建消费者标签
        simpleMessageListenerContainer.setConsumerTagStrategy(new ConsumerTagStrategy() {
            @Override
            public String createConsumerTag(String queue) {
                return queue;
            }
        });
        //监听适配器的定义
        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter();
        messageListenerAdapter.setDelegate(new MyMessageDelegate());
        //使用自带的json转换类
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        messageListenerAdapter.setMessageConverter(converter);
        //指定处理方法
        messageListenerAdapter.setDefaultListenerMethod("testClassHandle");
        //指定适配器
        simpleMessageListenerContainer.setMessageListener(messageListenerAdapter);
        return simpleMessageListenerContainer;
    }*/

    //直接处理传过来的Java对象
    //@Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer() {
        SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer(connectionFactory());
        simpleMessageListenerContainer.setQueues(queueC(), queueD());
        //一个消费者对象可以监听几个队列
        simpleMessageListenerContainer.setConcurrentConsumers(1);
        //最多创建几个消费者对象
        simpleMessageListenerContainer.setMaxConcurrentConsumers(5);
        //设置手动确认
        simpleMessageListenerContainer.setAcknowledgeMode(AcknowledgeMode.AUTO);
        //创建消费者标签
        simpleMessageListenerContainer.setConsumerTagStrategy(new ConsumerTagStrategy() {
            @Override
            public String createConsumerTag(String queue) {
                return queue;
            }
        });
        //监听适配器的定义
        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter();
        messageListenerAdapter.setDelegate(new MyMessageDelegate());
        //使用自带的json转换类
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        //Java对象映射器
        DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();
        //设置信任所有的包
        javaTypeMapper.setTrustedPackages("*");
        converter.setJavaTypeMapper(javaTypeMapper);
        messageListenerAdapter.setMessageConverter(converter);
        //指定处理方法
        messageListenerAdapter.setDefaultListenerMethod("testJavaClassHandle");
        //指定适配器
        simpleMessageListenerContainer.setMessageListener(messageListenerAdapter);
        return simpleMessageListenerContainer;
    }
}