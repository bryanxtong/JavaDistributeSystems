package com.example.grpcclient;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.grpc.sample.proto.SimpleGrpc;

@Configuration
public class StubConfiguration {
    @Bean
    Channel channel() {
        return ManagedChannelBuilder.forAddress("localhost",9090).usePlaintext().build();
    }
    @Bean
    SimpleGrpc.SimpleBlockingStub blockingStub() {
        return SimpleGrpc.newBlockingStub(channel());
    }

    @Bean
    @Lazy
    SimpleGrpc.SimpleBlockingV2Stub basic() {
        return SimpleGrpc.newBlockingV2Stub(channel());
    }

    @Bean
    SimpleGrpc.SimpleStub nonBlockingStub() {
        return SimpleGrpc.newStub(channel());
    }

    @Bean
    SimpleGrpc.SimpleFutureStub futureStub() {
        return SimpleGrpc.newFutureStub(channel());
    }
}
