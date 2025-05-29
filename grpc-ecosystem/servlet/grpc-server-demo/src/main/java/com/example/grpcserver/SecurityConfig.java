package com.example.grpcserver;

import net.devh.boot.grpc.server.security.authentication.BearerAuthenticationReader;
import net.devh.boot.grpc.server.security.authentication.GrpcAuthenticationReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import java.util.ArrayList;
import java.util.List;

/**
 * Grpc client send bearer token in the header and DefaultAuthenticatingServerInterceptor will use this class to
 * do the authentication. currently, reactive is not supported for security
 */
@Configuration(proxyBeanMethods = false)
public class SecurityConfig {
    @Bean
    GrpcAuthenticationReader authenticationReader() {
        return new BearerAuthenticationReader(token -> {
            if (token != null) {
                try {
                    return new BearerTokenAuthenticationToken(token);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        });
    }

    @Bean
    JwtAuthenticationProvider jwtAuthenticationProvider(JwtDecoder jwtDecoder) {
        return new JwtAuthenticationProvider(jwtDecoder);
    }

    // Add the authentication providers to the manager.
    @Bean
    AuthenticationManager authenticationManager(final JwtAuthenticationProvider jwtAuthenticationProvider) {
        final List<AuthenticationProvider> providers = new ArrayList<>();
        providers.add(jwtAuthenticationProvider);
        return new ProviderManager(providers);
    }
}