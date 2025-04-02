package com.example.rocketmq.push;

import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.function.Consumer;

@Component
public class BatchConsumer {
    @Bean
    public Consumer<Message<List<SimpleMsg>>> inputBatch() {
        return message -> {
            List<SimpleMsg> payloads = message.getPayload();
            System.out.println("Payloads received: " + payloads);
        };
    }
}