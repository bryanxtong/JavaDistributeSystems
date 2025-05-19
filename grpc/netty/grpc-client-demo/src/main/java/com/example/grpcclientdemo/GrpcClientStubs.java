package com.example.grpcclientdemo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.ChannelBuilderOptions;
import org.springframework.grpc.client.GrpcClientFactoryCustomizer;
import org.springframework.grpc.client.ImportGrpcClients;
import org.springframework.grpc.client.interceptor.security.BearerTokenAuthenticationInterceptor;
import org.springframework.grpc.sample.proto.SimpleGrpc;
import org.springframework.security.oauth2.client.endpoint.WebClientReactiveClientCredentialsTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import java.util.List;

@ImportGrpcClients(target = "blockingStub", types = { SimpleGrpc.SimpleBlockingStub.class })
@ImportGrpcClients(target = "blockingStubV2", types = { SimpleGrpc.SimpleBlockingV2Stub.class })
@ImportGrpcClients(target = "nonBlockingStub", types = { SimpleGrpc.SimpleStub.class })
@ImportGrpcClients(target = "futureStub", types = { SimpleGrpc.SimpleFutureStub.class })
@Configuration
public class GrpcClientStubs {

    private final static String target = "0.0.0.0:9090";
    private final static String GRPC_CLIENT_NAME = "grpc-client";
    @Bean
    public WebClientReactiveClientCredentialsTokenResponseClient reactiveTokenResponseClient() {
        return new WebClientReactiveClientCredentialsTokenResponseClient();
    }

    @Bean
    GrpcClientFactoryCustomizer grpcClientFactoryCustomizer(BlockingTokenSupplier tokenSupplier) {
        return registry -> {
            registry.channel("blockingStub", ChannelBuilderOptions.defaults()
                    .withInterceptors(List.of(new BearerTokenAuthenticationInterceptor(tokenSupplier))));
            registry.channel("blockingStubV2", ChannelBuilderOptions.defaults()
                    .withInterceptors(List.of(new BearerTokenAuthenticationInterceptor(tokenSupplier))));
            registry.channel("nonBlockingStub", ChannelBuilderOptions.defaults()
                    .withInterceptors(List.of(new BearerTokenAuthenticationInterceptor(tokenSupplier))));
            registry.channel("futureStub", ChannelBuilderOptions.defaults()
                    .withInterceptors(List.of(new BearerTokenAuthenticationInterceptor(tokenSupplier))));
        };
    }

    @Bean
    public BlockingTokenSupplier tokenBlockingGet(ClientRegistrationRepository clientRegistrationRepository, WebClientReactiveClientCredentialsTokenResponseClient client) {
        ClientRegistration reg = clientRegistrationRepository.findByRegistrationId(GRPC_CLIENT_NAME);
        return new BlockingTokenSupplier(reg,client);
    }
}
