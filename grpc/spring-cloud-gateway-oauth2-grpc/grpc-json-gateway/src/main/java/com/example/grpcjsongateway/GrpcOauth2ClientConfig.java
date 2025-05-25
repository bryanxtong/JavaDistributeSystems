package com.example.grpcjsongateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.*;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import java.util.List;

@Configuration
public class GrpcOauth2ClientConfig {

    @Bean
    public ReactiveClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryReactiveClientRegistrationRepository(this.grpcClientRegistration());
    }

    private ClientRegistration grpcClientRegistration() {
        return ClientRegistration.withRegistrationId("grpc-client")
                .clientId("grpc-client")
                .clientSecret("secret")
                .tokenUri("http://localhost:9000/oauth2/token")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .authorizationUri("http://localhost:9000/oauth2/authorize")
                .scope(List.of("openid", "profile"))
                .jwkSetUri("http://localhost:9000/oauth2/jwks")
                .issuerUri("http://localhost:9000")
                .build();
    }
}
