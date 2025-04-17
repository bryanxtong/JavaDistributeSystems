package com.example.gateway.oauth2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import java.util.Map;

@Configuration
public class CircuitBreakerFallBackEndpoints {
    @Bean
    public RouterFunction<ServerResponse> routerFunction() {
        return RouterFunctions
                .route()
                .GET("/user-fallback", userFallBackEndpoint())
                .POST("/user-fallback", userFallBackEndpoint())
                .build();
    }

    public HandlerFunction<ServerResponse> userFallBackEndpoint() {
        return request -> ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(Map.of("status", "SERVICE UNAVAILABLE",
                        "message", "User service is currently unavailable. Please try again later.",
                        "code", "503"))
                );
    }
}
