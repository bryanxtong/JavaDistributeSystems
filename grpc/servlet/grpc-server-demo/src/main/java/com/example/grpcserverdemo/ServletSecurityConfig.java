package com.example.grpcserverdemo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class ServletSecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/Simple/sayHello").hasAuthority("SCOPE_profile")
                        .requestMatchers("/Simple/streamHello").hasAuthority("SCOPE_profile")
                        .requestMatchers("/Simple/clientStreamHello").hasAuthority("SCOPE_profile")
                        .requestMatchers("/Simple/biStreamHello").hasAuthority("SCOPE_profile")
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }
}
