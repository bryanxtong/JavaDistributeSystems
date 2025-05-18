package com.example.grpcserverdemo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.server.GlobalServerInterceptor;
import org.springframework.grpc.server.security.AuthenticationProcessInterceptor;
import org.springframework.grpc.server.security.GrpcSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
@Configuration
@EnableWebFluxSecurity
public class GrpcServerSecurity {
    @Bean
    @GlobalServerInterceptor
    AuthenticationProcessInterceptor jwtSecurityFilterChain(GrpcSecurity grpc) throws Exception {
        System.out.println(grpc);
        return grpc.authorizeRequests(requests -> requests
                        .methods("Simple/sayHello").hasAuthority("SCOPE_profile")
                        .methods("Simple/streamHello").hasAuthority("SCOPE_profile")
                        .methods("Simple/clientStreamHello").hasAuthority("SCOPE_profile")
                        .methods("Simple/biStreamHello").hasAuthority("SCOPE_profile")
                        .methods("grpc.*/*").permitAll()
                        .allRequests().authenticated())
                .oauth2ResourceServer(oAuth2ResourceServerConfigurer -> oAuth2ResourceServerConfigurer.jwt(Customizer.withDefaults())).build();
    }
}
