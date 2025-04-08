package com.example.kafka.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.binder.PollableMessageSource;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * It only supports to poll one message each time now for PollableMessageSource for the official API.
 */
@SpringBootApplication
public class KafkaMessagePollConsumerApplication {
    @Autowired
    private StreamBridge streamBridge;
    private AtomicInteger counter = new AtomicInteger(0);

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(KafkaMessagePollConsumerApplication.class, args);
    }

    /**
     * Application Runner for sending messages, It is not spring cloud function
     * @return
     */
    @Bean
    public ApplicationRunner sendMessage() {
        return args -> {
            new Thread(() -> {
                while (true) {
                    try {
                        streamBridge.send("producer-out-0", new SimpleMsg("Test Sent Message: " + counter.incrementAndGet()));
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        };
    }

    /**
     * Not a spring cloud function
     * @param destIn
     * @return
     */
    @Bean
    public ApplicationRunner poller(PollableMessageSource destIn) {
        return args -> {
            new Thread(() -> {
                while (true) {
                    try {
                        if (!destIn.poll((m) -> {
                            SimpleMsg newPayload = (SimpleMsg) m.getPayload();
                            System.out.println("Message polled: " + newPayload);
                        }, new ParameterizedTypeReference<SimpleMsg>() {
                        })) {
                            Thread.sleep(1000);
                        }
                    } catch (Exception e) {
                        // handle failure
                    }
                }
            }).start();
        };
    }
}
