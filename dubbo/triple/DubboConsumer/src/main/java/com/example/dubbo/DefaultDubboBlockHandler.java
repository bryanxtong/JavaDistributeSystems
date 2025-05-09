package com.example.dubbo;

import com.alibaba.csp.sentinel.adapter.spring.webflux.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class DefaultDubboBlockHandler implements BlockRequestHandler {
    @Override
    public Mono<ServerResponse> handleRequest(ServerWebExchange exchange, Throwable t) {
        HttpStatus status;
        String body;
        if (t instanceof FlowException fe) {
            body = "Too Many Requests and Sentinel Blocked";
            status = HttpStatus.TOO_MANY_REQUESTS;
        } else if (t instanceof DegradeException de) {
            body = "Service Unavailable and Sentinel Degraded";
            status = HttpStatus.SERVICE_UNAVAILABLE;
        } else {
            body = "Internal Server Error";
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return ServerResponse.status(status).bodyValue(body);
    }
}
