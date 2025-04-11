package com.example.kafka.reactive;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Process the kafka messages with spring cloud stream kafka reactive
 */
@SpringBootApplication
@Slf4j
public class KafkaReactiveMessagePollBatchConsumerApplication {
    private AtomicInteger counter = new AtomicInteger(0);

    public static void main(String[] args) {
        SpringApplication.run(KafkaReactiveMessagePollBatchConsumerApplication.class, args);
    }

    @Bean
    public Supplier<Flux<SimpleMsg>> supplier() {
        return () -> Flux.interval(Duration.ofSeconds(1))
                .map(id -> new SimpleMsg(id, "Test Message"));
    }

    @Bean
    Function<Flux<SimpleMsg>, Flux<Message<SimpleMsg>>> process() {
        return simpleMsgs -> {
            return simpleMsgs
                    .map(simpleMsg -> MessageBuilder
                            .withPayload(new SimpleMsg(simpleMsg.getId(), simpleMsg.getMsg() + "--" + counter.incrementAndGet()))
                            .build());
        };

    }

    @Bean
    public Consumer<Flux<Message<SimpleMsg>>> consumer() {
        return flux -> flux
                .doOnNext(event -> {
                    log.info("Received: {}", event.getPayload());
                })
                .subscribe();
    }
}
