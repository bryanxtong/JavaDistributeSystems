package com.example.rocketmq.poll;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.cloud.stream.binder.PollableMessageSource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
@EnableScheduling
public class RocketMQPollExampleApplication {
    private final StreamBridge streamBridge;
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private AtomicInteger count = new AtomicInteger(0);

    private PollableMessageSource pollableMessageSource;

    public RocketMQPollExampleApplication(StreamBridge streamBridge, PollableMessageSource pollableMessageSource) {
        this.streamBridge = streamBridge;
        this.pollableMessageSource = pollableMessageSource;
    }

    public static void main(String[] args) {
        SpringApplication.run(RocketMQPollExampleApplication.class, args);
    }

    @Scheduled(fixedDelay = 10000)
    public void sendMessage() {
        String payload = "Message at " + dateFormat.format(new Date()) + count.incrementAndGet();
        SimpleMsg simpleMsg = new SimpleMsg(payload);
        streamBridge.send("messageSupplier-out-0", simpleMsg);
        System.out.println("[Producer] Sent: " + simpleMsg);
    }

    @Scheduled(fixedDelay = 10000)
    public void pollMessages() {
        boolean hasMessage = pollableMessageSource.poll(message -> {
                    Object payload = message.getPayload();
                    System.out.println("[Consumer] Received: " + payload);
                }, new ParameterizedTypeReference<SimpleMsg>(){});
        if (!hasMessage) {
            System.out.println("[Consumer] No messages available.");
        }
    }
}