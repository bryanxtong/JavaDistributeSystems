package com.example.grpcclientdemo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.grpc.client.ChannelBuilderOptions;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.grpc.client.interceptor.security.BearerTokenAuthenticationInterceptor;
import org.springframework.grpc.sample.proto.SimpleGrpc;
import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequest;
import org.springframework.security.oauth2.client.endpoint.RestClientClientCredentialsTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

@Configuration
public class ExtraConfiguration {
    private final static String target = "0.0.0.0:9090";
    private final static String GRPC_CLIENT_NAME = "grpc-client";
    private AtomicReference<String> token = new AtomicReference<>();
    private AtomicReference<Instant> expiresAt = new AtomicReference<>();

    @Bean
    @Lazy
    SimpleGrpc.SimpleBlockingStub basic(GrpcChannelFactory channels, ClientRegistrationRepository registry) {
        ClientRegistration reg = registry.findByRegistrationId(GRPC_CLIENT_NAME);
        return SimpleGrpc.newBlockingStub(channels.createChannel(target, ChannelBuilderOptions.defaults()
                .withInterceptors(List.of(new BearerTokenAuthenticationInterceptor(tokenSupplier(reg))))));
    }

    /**
     * provide a supplier to get called by interceptors each time It runs
     * @param reg
     * @return
     */
    private Supplier<String> tokenSupplier(ClientRegistration reg) {
        return ()->{
            Instant now = Instant.now();
            if (token.get() != null && expiresAt.get() != null && expiresAt.get().isAfter(now)) {
                return token.get();
            }
            RestClientClientCredentialsTokenResponseClient creds = new RestClientClientCredentialsTokenResponseClient();
            OAuth2AccessToken accessToken = creds.getTokenResponse(new OAuth2ClientCredentialsGrantRequest(reg))
                    .getAccessToken();
            this.token.set(accessToken.getTokenValue());
            this.expiresAt.set(accessToken.getExpiresAt());
            System.out.println("token: " + token);
            return token.get();
        };
    }

    @Bean
    SimpleGrpc.SimpleBlockingV2Stub blockingStub2(GrpcChannelFactory grpcChannelFactory, ClientRegistrationRepository clientRegistrationRepository) {
        ClientRegistration reg = clientRegistrationRepository.findByRegistrationId(GRPC_CLIENT_NAME);
        return SimpleGrpc.newBlockingV2Stub(grpcChannelFactory.createChannel(target, ChannelBuilderOptions.defaults().withInterceptors(List.of(new BearerTokenAuthenticationInterceptor(tokenSupplier(reg))))));
    }

    @Bean
    SimpleGrpc.SimpleStub nonBlockingStub(GrpcChannelFactory grpcChannelFactory, ClientRegistrationRepository clientRegistrationRepository) {
        ClientRegistration reg = clientRegistrationRepository.findByRegistrationId(GRPC_CLIENT_NAME);
        return SimpleGrpc.newStub(grpcChannelFactory.createChannel(target, ChannelBuilderOptions.defaults().withInterceptors(List.of(new BearerTokenAuthenticationInterceptor(tokenSupplier(reg))))));
    }

    @Bean
    SimpleGrpc.SimpleFutureStub futureStub(GrpcChannelFactory grpcChannelFactory, ClientRegistrationRepository clientRegistrationRepository) {
        ClientRegistration reg = clientRegistrationRepository.findByRegistrationId(GRPC_CLIENT_NAME);
        return SimpleGrpc.newFutureStub(grpcChannelFactory.createChannel(target, ChannelBuilderOptions.defaults().withInterceptors(List.of(new BearerTokenAuthenticationInterceptor(tokenSupplier(reg))))));
    }
}
