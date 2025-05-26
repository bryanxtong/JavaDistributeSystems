package com.example.grpcclientdemo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.config.EnableWebFlux;
import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebFlux
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeExchange(authorizeRequests ->
                        authorizeRequests
                                .pathMatchers("/api/public/**").permitAll()
                                .anyExchange().authenticated()
                )
                .formLogin(withDefaults())
                .httpBasic(withDefaults());

        return httpSecurity.build();
    }
}

