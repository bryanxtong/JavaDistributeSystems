package com.example.rocketmq.push;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RocketMQMessagePushExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(RocketMQMessagePushExampleApplication.class, args);
    }
}
