package com.example.rocketmq.push;

import org.springframework.cloud.stream.function.StreamBridge;
import java.util.Date;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * It seems rocketmq doesn't support to consume batch of messages in one time.
 * If we send records to rocketmq messages one by one and the consumer will consume the messages one by one even in batch mode,
 * but we can send batch of messages to rocketmq(A single messageId) and consumer will consume all of them in one batch.
 */
@Configuration
public class BatchProducer {
    private final StreamBridge streamBridge;
    private AtomicInteger count = new AtomicInteger(0);
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public BatchProducer(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    @Scheduled(fixedRate = 5000)
    public void sendBatch() {
        List<SimpleMsg> simpleMsgList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            String payload = "Message at " + dateFormat.format(new Date()) + count.incrementAndGet();
            simpleMsgList.add(new SimpleMsg(payload));
        }
        streamBridge.send("outputBatch-out-0", simpleMsgList);
        System.out.println("Sent batch: " + simpleMsgList);
    }
}
