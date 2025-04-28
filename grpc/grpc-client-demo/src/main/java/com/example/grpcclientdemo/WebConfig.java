package com.example.grpcclientdemo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.WebFilter;

@Configuration
public class WebConfig {
    @Bean
    public WebFilter webFilter() {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            System.out.println("Content Type: " + request.getHeaders().getContentType());
            return chain.filter(exchange);
        };
    }
}
