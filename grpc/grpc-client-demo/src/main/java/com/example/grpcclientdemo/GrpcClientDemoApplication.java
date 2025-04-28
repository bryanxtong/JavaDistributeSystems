package com.example.grpcclientdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.grpc.sample.proto.SimpleGrpc;

@SpringBootApplication
public class GrpcClientDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrpcClientDemoApplication.class, args);
    }

    @Bean
    SimpleGrpc.SimpleBlockingStub blockingStub(GrpcChannelFactory grpcChannelFactory) {
        return SimpleGrpc.newBlockingStub(grpcChannelFactory.createChannel("local"));
    }

    @Bean
    SimpleGrpc.SimpleBlockingV2Stub blockingStub2(GrpcChannelFactory grpcChannelFactory) {
        return SimpleGrpc.newBlockingV2Stub(grpcChannelFactory.createChannel("local"));
    }

    @Bean
    SimpleGrpc.SimpleStub nonBlockingStub(GrpcChannelFactory grpcChannelFactory) {
        return SimpleGrpc.newStub(grpcChannelFactory.createChannel("local"));
    }

    @Bean
    SimpleGrpc.SimpleFutureStub futureStub(GrpcChannelFactory grpcChannelFactory) {
        return SimpleGrpc.newFutureStub(grpcChannelFactory.createChannel("local"));
    }
}
