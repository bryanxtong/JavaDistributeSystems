package com.example.grpcclientdemo;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.ChannelBuilderOptions;
import org.springframework.grpc.client.GrpcClientFactoryCustomizer;
import org.springframework.grpc.client.ImportGrpcClients;
import org.springframework.grpc.client.interceptor.security.BearerTokenAuthenticationInterceptor;
import org.springframework.grpc.sample.proto.SimpleGrpc;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import java.util.List;

@Configuration
@ImportGrpcClients(target = "blockingStub", types = { SimpleGrpc.SimpleBlockingStub.class })
@ImportGrpcClients(target = "blockingStubV2", types = { SimpleGrpc.SimpleBlockingV2Stub.class })
@ImportGrpcClients(target = "nonBlockingStub", types = { SimpleGrpc.SimpleStub.class })
@ImportGrpcClients(target = "futureStub", types = { SimpleGrpc.SimpleFutureStub.class })
public class StubConfiguration {
    public static final String target = "0.0.0.0:9090";
    public static String GRPC_CLIENT_NAME = "grpc-client";

    /**
     * It is better to have a token for each user, but we don't have users info at this place.
     * Currently for each other, all the users share the same BlockingTokenSupplier instance.
     * providing a supplier here, just to ensure interceptor can be called each time to refresh
     * the token if expired.
     * @param context
     * @return
     */
    @Bean
    GrpcClientFactoryCustomizer blockingStub(ObjectProvider<ClientRegistrationRepository> context) {
        return registry -> {
            registry.channel("blockingStub", ChannelBuilderOptions.defaults()
                    .withInterceptors(List.of(new BearerTokenAuthenticationInterceptor(new BlockingTokenSupplier(context)))));
            registry.channel("blockingStubV2", ChannelBuilderOptions.defaults()
                    .withInterceptors(List.of(new BearerTokenAuthenticationInterceptor(new BlockingTokenSupplier(context)))));
            registry.channel("nonBlockingStub", ChannelBuilderOptions.defaults()
                    .withInterceptors(List.of(new BearerTokenAuthenticationInterceptor(new BlockingTokenSupplier(context)))));
            registry.channel("futureStub", ChannelBuilderOptions.defaults()
                    .withInterceptors(List.of(new BearerTokenAuthenticationInterceptor(new BlockingTokenSupplier(context)))));
        };
    }


}
