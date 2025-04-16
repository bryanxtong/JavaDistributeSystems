package com.example.appservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api")
public class TestEndpoint {
    @GetMapping("/user/{id}")
    public Mono<String> user(@PathVariable String id) {
        return Mono.justOrEmpty("user-" + id );
    }

    @GetMapping("/products/{id}")
    public Mono<String> product(@PathVariable String id) {
        return Mono.justOrEmpty("product-" + id);
    }

    @GetMapping("/admin/{id}")
    public Mono<String> admin(@PathVariable String id) {
        return Mono.justOrEmpty("admin-" + id);
    }

    @GetMapping("/manager/{id}")
    public Mono<String> manager(@PathVariable String id) {
        return Mono.justOrEmpty("manager-" + id);
    }
}
