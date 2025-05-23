package com.example.gateway.oauth2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * This spring cloud gateway forwards to keycloak and get the jwt and token-relay it
 * The role is processed in separate microservices
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    private Logger log = LoggerFactory.getLogger(SecurityConfig.class);
    private final ReactiveClientRegistrationRepository clientRegistrationRepository;

    public SecurityConfig(ReactiveClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    /**
     * No roles configuration, and role config moves to separate microservices
     *
     * @param http
     * @return
     */
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.authorizeExchange(exchanges -> {
                    exchanges.pathMatchers("/", "/login", "/webjars/**", "/static/**","/user-fallback").permitAll()
                            .pathMatchers("/api/public/**").permitAll()
                            .anyExchange().authenticated();
                })
                .oauth2Login(Customizer.withDefaults())
                .oauth2Client(Customizer.withDefaults())
                //in case you want to test with authorization header in postman(test senario)
                //.oauth2ResourceServer(oAuth2ResourceServerSpec -> oAuth2ResourceServerSpec.jwt(jwtSpec -> jwtSpec.jwkSetUri("http://localhost:8080/realms/myrealm/protocol/openid-connect/certs")))
                .csrf(ServerHttpSecurity.CsrfSpec::disable);
        return http.build();
    }

}
