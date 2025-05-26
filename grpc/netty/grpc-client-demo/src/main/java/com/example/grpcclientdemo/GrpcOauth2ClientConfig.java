package com.example.grpcclientdemo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import java.util.List;

@Configuration
public class GrpcOauth2ClientConfig {

    /**
     * for local client only
     * @return
     */
    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        UserDetails user = User.withUsername("user")
                .password("{noop}user")
                .roles("USER")
                .build();
        UserDetails admin = User.withUsername("admin")
                .password("{noop}admin")
                .roles("ADMIN")
                .build();
        return new MapReactiveUserDetailsService(user, admin);
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(this.grpcClientRegistration());
    }

    private ClientRegistration grpcClientRegistration() {
        return ClientRegistration.withRegistrationId("grpc-client")
                .clientId("grpc-client")
                .clientSecret("secret")
                .tokenUri("http://localhost:9000/oauth2/token")
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .scope(List.of("openid", "profile"))
                .build();
    }
}

