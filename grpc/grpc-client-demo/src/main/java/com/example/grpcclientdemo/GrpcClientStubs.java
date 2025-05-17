package com.example.grpcclientdemo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.grpc.client.ChannelBuilderOptions;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.grpc.client.interceptor.security.BearerTokenAuthenticationInterceptor;
import org.springframework.grpc.sample.proto.SimpleGrpc;
import org.springframework.security.oauth2.client.endpoint.WebClientReactiveClientCredentialsTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import java.util.List;

@Configuration
public class GrpcClientStubs {
    private final static String target = "0.0.0.0:9090";
    private final static String GRPC_CLIENT_NAME = "grpc-client";
    @Bean
    public WebClientReactiveClientCredentialsTokenResponseClient reactiveTokenResponseClient() {
        return new WebClientReactiveClientCredentialsTokenResponseClient();
    }

    @Bean
    @Lazy
    SimpleGrpc.SimpleBlockingStub basic(GrpcChannelFactory grpcChannelFactory, ClientRegistrationRepository clientRegistrationRepository, BlockingTokenSupplier tokenSupplier) {
        ClientRegistration reg = clientRegistrationRepository.findByRegistrationId(GRPC_CLIENT_NAME);
        return SimpleGrpc.newBlockingStub(grpcChannelFactory.createChannel(target, ChannelBuilderOptions.defaults().withInterceptors(List.of(new BearerTokenAuthenticationInterceptor(tokenSupplier)))));
    }

    @Bean
    public BlockingTokenSupplier tokenBlockingGet(ClientRegistrationRepository clientRegistrationRepository, WebClientReactiveClientCredentialsTokenResponseClient client) {
        ClientRegistration reg = clientRegistrationRepository.findByRegistrationId(GRPC_CLIENT_NAME);
        return new BlockingTokenSupplier(reg,client);
    }

    @Bean
    SimpleGrpc.SimpleBlockingV2Stub blockingStub2(GrpcChannelFactory grpcChannelFactory, ClientRegistrationRepository clientRegistrationRepository,BlockingTokenSupplier tokenSupplier) {
        ClientRegistration reg = clientRegistrationRepository.findByRegistrationId(GRPC_CLIENT_NAME);
        return SimpleGrpc.newBlockingV2Stub(grpcChannelFactory.createChannel(target, ChannelBuilderOptions.defaults().withInterceptors(List.of(new BearerTokenAuthenticationInterceptor(tokenSupplier)))));
    }

    @Bean
    SimpleGrpc.SimpleStub nonBlockingStub(GrpcChannelFactory grpcChannelFactory, ClientRegistrationRepository clientRegistrationRepository,BlockingTokenSupplier tokenSupplier) {
        ClientRegistration reg = clientRegistrationRepository.findByRegistrationId(GRPC_CLIENT_NAME);
        return SimpleGrpc.newStub(grpcChannelFactory.createChannel(target, ChannelBuilderOptions.defaults().withInterceptors(List.of(new BearerTokenAuthenticationInterceptor(tokenSupplier)))));
    }

    @Bean
    SimpleGrpc.SimpleFutureStub futureStub(GrpcChannelFactory grpcChannelFactory, ClientRegistrationRepository clientRegistrationRepository,BlockingTokenSupplier tokenSupplier) {
        ClientRegistration reg = clientRegistrationRepository.findByRegistrationId(GRPC_CLIENT_NAME);
        return SimpleGrpc.newFutureStub(grpcChannelFactory.createChannel(target, ChannelBuilderOptions.defaults().withInterceptors(List.of(new BearerTokenAuthenticationInterceptor(tokenSupplier)))));
    }
}
