package com.qdbh.testrabbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qdbh.testrabbitmq.a91_spring_listener_adapter.MyOrder;
import com.qdbh.testrabbitmq.controller.TestController;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SpringBootTest
class TestRabbitmqApplicationTests {

    //引入Bean
    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Test
    void contextLoads() {
    }

    @Test
    void testDemoOne() {

        //声明队列
        Queue queue = new Queue("newSpringAMQPQueue", true, false, false, null);
        rabbitAdmin.declareQueue(queue);
        //· 声明交换机

        //·直连型
        Exchange exchange = new DirectExchange("newSpringAMQPDirectExchange", true, false, null);
        rabbitAdmin.declareExchange(exchange);
        //·fanout型
        Exchange exchangeFanout = new FanoutExchange("SpringAMQPFanoutExchange", true, false, null);
        rabbitAdmin.declareExchange(exchangeFanout);
        //·topic型
        Exchange exchangeTopic = new TopicExchange("SpringAMQPTopicExchange", true, false, null);
        rabbitAdmin.declareExchange(exchangeTopic);
        //·header型
        HashMap<String, Object> arguments = new HashMap<>();
        arguments.put("x-match", "all");
        arguments.put("tag", "amqp");
        Exchange exchangeHeaders = new HeadersExchange("SpringAMQPHeadersExchange", true, false, arguments);
        rabbitAdmin.declareExchange(exchangeHeaders);

        //进行绑定
        //第二个参数 要绑定是队列和交换机还是交换机和交换机 第一个参数 交换机或者队列的名字
        Binding binding = new Binding("newSpringAMQPQueue", Binding.DestinationType.QUEUE, "newSpringAMQPDirectExchange"
                , "biu~", null);
        rabbitAdmin.declareBinding(binding);
        /*
            绑定的另一种写法
         */
        /*rabbitAdmin.declareBinding(
                BindingBuilder
                        .bind(queue)
                        .to(exchange)
                        .with("biu~")
                        .and(null)
        );*/
    }

    @Test
    void purgeQueueAndDeleteQueue() {

        //清空队列
        rabbitAdmin.purgeQueue("Test队列大小限制");

        //删除队列
        rabbitAdmin.deleteQueue("newSpringAMQPQueue");

        //删除交换机
        rabbitAdmin.deleteExchange("newSpringAMQPDirectExchange");
    }

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    void testSendOne() {

        String msg = "尝试向交换机biu~ 一条短信";
        rabbitTemplate.convertAndSend("newSpringAMQPDirectExchange", "biu~", msg, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().getHeaders().put("附加信息", "使用MessagePostProcessor添加头部信息");
                return message;
            }
        });
    }

    //尝试向QueueC发送一个Order类 我们将类转换为Json对象
    @Test
    void testSendOrderClassParseToJson() throws JsonProcessingException {

        MyOrder myOrder = new MyOrder(777, "尝试向QueueC发送一个Order类 我们将类转换为Json对象", BigDecimal.valueOf(9999.789));
        //将Order对象转换为Json对象
        ObjectMapper objectMapper = new ObjectMapper();
        String valueAsString = objectMapper.writeValueAsString(myOrder);
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentEncoding("UTF-8");
        messageProperties.setContentType("application/json");
        rabbitTemplate.send("direct_exchange", "email", new Message(valueAsString.getBytes(), messageProperties));
    }

    //直接向QueueC发送一个Order类对象
    @Test
    void testSendOrderClass() throws JsonProcessingException {

        MyOrder myOrder = new MyOrder(777, "直接向QueueC发送一个Order类对象", BigDecimal.valueOf(9999.789));
        //将Order对象转换为Json对象
        ObjectMapper objectMapper = new ObjectMapper();
        String valueAsString = objectMapper.writeValueAsString(myOrder);
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentEncoding("UTF-8");
        messageProperties.setContentType("application/json");
        //设置对应的类 需要写包名以及类名 不推荐使用
        messageProperties.getHeaders().put(" _TypeId_ ", "com/qdbh/testrabbitmq/a91_spring_listener_adapter/MyOrder.java");
        rabbitTemplate.send("direct_exchange", "email", new Message(valueAsString.getBytes(), messageProperties));
    }

    //使用spring整合后发送类对象
    @Test
    void sendOrderClass() throws JsonProcessingException {

        MyOrder myOrder = new MyOrder(777, "直接向QueueC发送一个Order类对象", BigDecimal.valueOf(9999.789));
        rabbitTemplate.convertAndSend("direct_exchange", "email", myOrder);
    }

    @Test
    void testSend() {

        MessageProperties messageProperties = new MessageProperties();
        messageProperties.getHeaders().put("附加信息", "使用MessageProperties添加头部信息");
        String msg = "尝试使用Send()";
        Message message = new Message(msg.getBytes(), messageProperties);
        rabbitTemplate.send("newSpringAMQPDirectExchange", "biu~", message);
    }

    @Autowired
    TestController testController;

    //测试
    @Test
    void testCorrelation() {

        MessageProperties messageProperties = new MessageProperties();
        messageProperties.getHeaders().put("附加信息", "使用MessageProperties添加头部信息");
        String msg = "尝试使用Send()";
        Message message = new Message(msg.getBytes(), messageProperties);
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString().concat("后缀"));
        //调用Common方法
        testController.commonSend("newSpringAMQPDirectExchange", message, "biu~", correlationData);
    }


    @Test
    void testReceive() {
        while (true) {

            Message newSpringAMQPQueue = rabbitTemplate.receive("newSpringAMQPQueue"); //默认一次消费一条消息 所以写个while死循环
            if (null == newSpringAMQPQueue)
                return;
            //获取msg
            String str = new String(newSpringAMQPQueue.getBody());
            System.out.println("收到消息：" + str);
            //获取headers信息
            Map<String, Object> headers = newSpringAMQPQueue.getMessageProperties().getHeaders();
            headers.forEach((k, v) -> {

                System.out.println("循环输出headers -- k：" + k + " ---- v：" + v);
            });
        }
    }

}
