package com.example.grpcclient.sd;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.NameResolverRegistry;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.grpc.sample.proto.SimpleGrpc;

@Configuration
public class StubConfiguration {
    public static final String SCHEME = "grpc";
    public static final String serviceName = "grpc-server-demo";

    @Bean
    Channel channel(DiscoveryClient discoveryClient) {
        //use NameResolver to implement a simple service discovery and scheduled a 5-seconds refresh
        NameResolverRegistry.getDefaultRegistry().register(new SpringCloudNameResolverProvider(discoveryClient));
        String target = String.format("%s:///%s", SCHEME, serviceName);
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target).defaultLoadBalancingPolicy("round_robin").usePlaintext().build();
        return channel;
    }

    @Bean
    SimpleGrpc.SimpleBlockingStub blockingStub(Channel channel) {
        return SimpleGrpc.newBlockingStub(channel);
    }

    @Bean
    @Lazy
    SimpleGrpc.SimpleBlockingV2Stub basic(Channel channel) {
        return SimpleGrpc.newBlockingV2Stub(channel);
    }

    @Bean
    SimpleGrpc.SimpleStub nonBlockingStub(Channel channel) {
        return SimpleGrpc.newStub(channel);
    }

    @Bean
    SimpleGrpc.SimpleFutureStub futureStub(Channel channel) {
        return SimpleGrpc.newFutureStub(channel);
    }
}
