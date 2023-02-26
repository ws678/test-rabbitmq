package com.qdbh.testrabbitmq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.qdbh.testrabbitmq"})
public class TestRabbitmqApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestRabbitmqApplication.class, args);
    }

}
