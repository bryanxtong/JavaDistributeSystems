package com.example.kafka.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * It supports to consume the messages from the topic in batch for kafka
 */
@SpringBootApplication
public class KafkaMessagePollBatchConsumerApplication {
    private AtomicInteger counter = new AtomicInteger(0);

    public static void main(String[] args) {
        SpringApplication.run(KafkaMessagePollBatchConsumerApplication.class, args);
    }

    @Bean
    public Supplier<SimpleMsg> supplier() {
       return ()-> {
           SimpleMsg simpleMsg = new SimpleMsg("Test message sent: " + counter.incrementAndGet());
           System.out.println("sending messages ===>" + simpleMsg);
           return simpleMsg;
       };
    }

    @Bean
    public Consumer<List<SimpleMsg>> consumer(){
        return msg->{
            System.out.println("msg received: "+msg);
        };
    }
}
