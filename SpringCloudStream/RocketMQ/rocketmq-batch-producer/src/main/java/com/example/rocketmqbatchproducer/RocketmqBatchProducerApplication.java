package com.example.rocketmqbatchproducer;

import org.apache.rocketmq.client.core.RocketMQClientTemplate;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.stream.IntStream;

@SpringBootApplication
public class RocketmqBatchProducerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RocketmqBatchProducerApplication.class, args);
    }

    @Bean
    public ApplicationRunner sendTestMessages1(RocketMQClientTemplate template) {
        return args -> {
            List<SimpleMsg> batch = IntStream.range(0, 10)
                    .mapToObj(i -> new SimpleMsg("Msg-" + i))
                    .toList();
            template.syncSendNormalMessage("continuous-topic", batch);
        };
    }
}
