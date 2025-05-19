package com.example.grpcclientdemo;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.ChannelBuilderOptions;
import org.springframework.grpc.client.GrpcClientFactoryCustomizer;
import org.springframework.grpc.client.ImportGrpcClients;
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
@ImportGrpcClients(target = "blockingStub", types = { SimpleGrpc.SimpleBlockingStub.class })
@ImportGrpcClients(target = "blockingStubV2", types = { SimpleGrpc.SimpleBlockingV2Stub.class })
@ImportGrpcClients(target = "nonBlockingStub", types = { SimpleGrpc.SimpleStub.class })
@ImportGrpcClients(target = "futureStub", types = { SimpleGrpc.SimpleFutureStub.class })
public class StubConfiguration {
    private final static String target = "0.0.0.0:9090";
    private final static String GRPC_CLIENT_NAME = "grpc-client";
    private AtomicReference<String> token = new AtomicReference<>();
    private AtomicReference<Instant> expiresAt = new AtomicReference<>();

    @Bean
    GrpcClientFactoryCustomizer blockingStub(ObjectProvider<ClientRegistrationRepository> context) {
        return registry -> {
            registry.channel("blockingStub", ChannelBuilderOptions.defaults()
                    .withInterceptors(List.of(new BearerTokenAuthenticationInterceptor(tokenSupplier(context)))));
            registry.channel("blockingStubV2", ChannelBuilderOptions.defaults()
                    .withInterceptors(List.of(new BearerTokenAuthenticationInterceptor(tokenSupplier(context)))));
            registry.channel("nonBlockingStub", ChannelBuilderOptions.defaults()
                    .withInterceptors(List.of(new BearerTokenAuthenticationInterceptor(tokenSupplier(context)))));
            registry.channel("futureStub", ChannelBuilderOptions.defaults()
                    .withInterceptors(List.of(new BearerTokenAuthenticationInterceptor(tokenSupplier(context)))));
        };
    }

    /**
     * provide a supplier to get called by interceptors each time It runs
     * @return
     */
    private Supplier<String> tokenSupplier(ObjectProvider<ClientRegistrationRepository> context) {
        return ()->{
            Instant now = Instant.now();
            if (token.get() != null && expiresAt.get() != null && expiresAt.get().isAfter(now)) {
                return token.get();
            }
            RestClientClientCredentialsTokenResponseClient creds = new RestClientClientCredentialsTokenResponseClient();
            ClientRegistrationRepository registry = context.getObject();
            ClientRegistration reg = registry.findByRegistrationId(GRPC_CLIENT_NAME);

            OAuth2AccessToken accessToken = creds.getTokenResponse(new OAuth2ClientCredentialsGrantRequest(reg))
                    .getAccessToken();
            this.token.set(accessToken.getTokenValue());
            this.expiresAt.set(accessToken.getExpiresAt());
            System.out.println("token: " + token);
            return token.get();
        };
    }
}
