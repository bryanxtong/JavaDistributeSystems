package com.example.dubbo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v2")
public class SimpleControllerV2 {

    @Autowired
    SimpleServiceClient simpleServiceClient;

    @GetMapping("/sayHello")
    public Mono<String> sayHello(@RequestParam("name") String name) {
        return Mono.create(sink -> {
            simpleServiceClient.sayHello(
                    name,
                    sink::success,
                    sink::error
            );
        });
    }

    /**
     * Server Streaming RPC: streamHello
     */
    @GetMapping(value = "/streamHello"/*, produces = "text/event-stream"*/)
    public Flux<String> streamHello(@RequestParam("name") String name) {
        return Flux.create(sink -> {
            simpleServiceClient.streamHello(
                    name,
                    sink::next,
                    sink::complete,
                    sink::error
            );
        });
    }

    /**
     * Client Streaming RPC: clientStreamHello
     */
    @PostMapping("/clientStreamHello")
    public Mono<String> clientStreamHello(@RequestBody List<String> names) {
        return Mono.create(sink -> {
            simpleServiceClient.clientStreamHello(helloRequestStreamObserver -> {
                        names.forEach(name -> helloRequestStreamObserver.onNext(new HelloRequest(name)));
                        helloRequestStreamObserver.onCompleted();
                    },
                    sink::success,
                    sink::error);
        });
    }

    @PostMapping(value = "/biStreamHello", consumes = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<String> biStreamHello(@RequestBody Flux<String> names) {
        return Flux.create(sink -> {
            simpleServiceClient.biStreamHello(
                    requestObserver -> {
                        names.subscribe(
                                (String name) -> requestObserver.onNext(new HelloRequest(name)),
                                throwable -> requestObserver.onError(throwable),
                                requestObserver::onCompleted
                        );
                    },
                    (observer, reply) -> {
                        sink.next(reply.getMessage());
                    },
                    throwable -> {
                        sink.error(throwable);
                    },
                    () -> {
                        sink.complete();
                    }
            );
        });
    }
}