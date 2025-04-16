package com.example.gateway.oauth2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

@Configuration
public class CircuitBreakerFallBackEndpoints {
    @Bean
    public RouterFunction<ServerResponse> routerFunction() {
        return RouterFunctions
                .route()
                .GET("/user-fallback", userFallBackGetEndpoint())
                .POST("/user-fallback", userFallBackPostEndpoint())
                .build();
    }

    public HandlerFunction<ServerResponse> userFallBackGetEndpoint() {
        return request -> ServerResponse.ok().body(Mono.just("user fall back due to circuitbreaker"), String.class);
    }

    public HandlerFunction<ServerResponse> userFallBackPostEndpoint() {
        return request -> ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }
}
