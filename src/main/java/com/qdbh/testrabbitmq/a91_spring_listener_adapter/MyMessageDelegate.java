package com.qdbh.testrabbitmq.a91_spring_listener_adapter;

import java.util.Map;

public class MyMessageDelegate {

    //默认会执行的处理方法
    public void handleMessage(byte[] body) {

        String s = new String(body);
        System.out.println("打印一下:" + s);
    }

    //自定义处理方法
    public void myHandleMessage(byte[] body) {
        String s = new String(body);
        System.out.println("自定义---打印一下:" + s);
    }

    //queueC处理方法
    public void queueCHandleMessage(byte[] body) {
        String s = new String(body);
        System.out.println("queueC---打印一下:" + s);
    }

    //queueD处理方法
    public void queueDHandleMessage(byte[] body) {
        String s = new String(body);
        System.out.println("queueD---打印一下:" + s);
    }

    //Test传输Order类处理方法
    public void testClassHandle(Map<String, Object> map) {
        map.forEach((k, v) -> {
            System.out.println("k: " + k + "\nv: " + v + "\n------------------------------");
        });
    }

    //Test传输Java Order类处理方法
    public void testJavaClassHandle(MyOrder order) {
        System.out.println(order.toString());
    }

    //使用自定义的转换类
    public void myConverter(Map<String, Object> map) {
        map.forEach((k, v) -> {
            System.out.println("k: " + k + "\nv: " + v + "\n------------------------------");
        });
    }
}
