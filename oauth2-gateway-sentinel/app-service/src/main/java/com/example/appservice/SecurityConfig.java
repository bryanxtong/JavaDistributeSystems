package com.example.appservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.codec.CodecCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    private Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, CodecCustomizer jacksonCodecCustomizer) {
        http.authorizeExchange(auth ->
                        auth.pathMatchers("/api/admin/**").hasAnyRole("ADMIN")
                                .pathMatchers("/api/manager/**").hasAnyRole("ADMIN", "MANAGER")
                                .anyExchange().authenticated())
                .oauth2ResourceServer(oAuth2ResourceServerSpec -> oAuth2ResourceServerSpec.jwt(jwtSpec ->
                        jwtSpec.jwtAuthenticationConverter(jwtAuthenticationConverter())));
        http.csrf(ServerHttpSecurity.CsrfSpec::disable);
        return http.build();
    }

    public Converter<Jwt, ? extends Mono<? extends AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        return (Converter<Jwt, Mono<? extends AbstractAuthenticationToken>>) jwt -> {
            log.info("Incoming JWT Claims: {}", jwt.getClaims());
            //read from keycloak realm_access
            Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
            List<String> roles = (List<String>) realmAccess.getOrDefault("roles", Collections.emptyList());
            List<SimpleGrantedAuthority> authorities = roles.stream()
                    //change to spring security format
                    .map(role -> "ROLE_" + role.toUpperCase())
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
            return Mono.just(new JwtAuthenticationToken(jwt, authorities));
        };
    }

}