package com.example.dubbo;

import org.apache.dubbo.common.stream.StreamObserver;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.grpc.sample.proto.HelloReply;
import org.springframework.grpc.sample.proto.HelloRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SimpleController {

    @DubboReference(loadbalance = "roundrobin")
    private SimpleService simpleService;

    /**
     * Unary RPC: sayHello
     */
    @GetMapping("/sayHello")
    public Mono<String> sayHello(@RequestParam("name") String name) {
        return Mono.fromCallable(() -> simpleService.sayHello(HelloRequest.newBuilder().setName(name).build()).getMessage());
    }

    /**
     * Server Streaming RPC: streamHello
     */
    @GetMapping(value = "/streamHello"/*, produces = "text/event-stream"*/)
    public Flux<String> streamHello(@RequestParam("name") String name) {
        return Flux.create(emitter -> {
            simpleService.streamHello(HelloRequest.newBuilder().setName(name).build(), new StreamObserver<>() {
                @Override
                public void onNext(HelloReply helloReply) {
                    emitter.next(helloReply.getMessage());
                }
                @Override
                public void onError(Throwable throwable) {
                    emitter.error(throwable);
                }
                @Override
                public void onCompleted() {
                    emitter.complete();
                }
            });
        });
    }

    /**
     * Client Streaming RPC: clientStreamHello
     * curl -X POST http://192.168.71.128:8090/api/clientStreamHello -H "Content-Type: application/json" -d '["Alice", "Bob", "Charlie"]'
     */
    @PostMapping("/clientStreamHello")
    public Mono<String> clientStreamHello(@RequestBody List<String> names) {
        return Mono.create(emitter -> {
            StreamObserver<HelloRequest> requestObserver = simpleService.clientStreamHello(new StreamObserver<>() {
                @Override
                public void onNext(HelloReply helloReply) {
                    emitter.success(helloReply.getMessage());
                }
                @Override
                public void onError(Throwable throwable) {
                    emitter.error(throwable);
                }
                @Override
                public void onCompleted() {
                }
            });
            names.forEach(name -> requestObserver.onNext(HelloRequest.newBuilder().setName(name).build()));
            requestObserver.onCompleted();
        });
    }

    /**
     * Bidirectional Streaming RPC: biStreamHello
     * curl -X POST http://192.168.71.116:8090/api/biStreamHello -H "Content-Type: application/x-ndjson" --data-binary $'"Alice"\n"Bob"\n'
     */
    @PostMapping(value = "/biStreamHello"/*, produces = "text/event-stream"*/, consumes = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<String> biStreamHello(@RequestBody Flux<String> names) {
        return Flux.create(emitter -> {
            StreamObserver<HelloRequest> requestObserver = simpleService.biStreamHello(new StreamObserver<>() {
                @Override
                public void onNext(HelloReply helloReply) {
                    emitter.next(helloReply.getMessage());
                }
                @Override
                public void onError(Throwable throwable) {
                    emitter.error(throwable);
                }
                @Override
                public void onCompleted() {
                    emitter.complete();
                }
            });
            names.subscribe((String name) -> requestObserver.onNext(HelloRequest.newBuilder().setName(name).build()),
                    error -> requestObserver.onError(error),
                    () -> requestObserver.onCompleted());
        });
    }
}