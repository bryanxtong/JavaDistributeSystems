package com.example.grpcclientdemo;

import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.sample.proto.HelloReply;
import org.springframework.grpc.sample.proto.HelloRequest;
import org.springframework.grpc.sample.proto.SimpleGrpc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("api")
public class ServerStreamController {
    @Autowired
    SimpleGrpc.SimpleStub simpleStub;

    /**
     * Server Stream
     */
    @GetMapping("/serverStream/{name}")
    public Flux<String> serverStream(@PathVariable String name) {
        return Flux.create(emitter -> {
            StreamObserver<HelloReply> streamObserver = new StreamObserver<>() {
                @Override
                public void onNext(HelloReply helloReply) {
                    emitter.next(helloReply.getMessage());
                }

                @Override
                public void onError(Throwable throwable) {
                    System.err.println(throwable.getMessage());
                    emitter.error(throwable);
                }

                @Override
                public void onCompleted() {
                    emitter.complete();
                }
            };
            simpleStub.streamHello(HelloRequest.newBuilder().setName(name).build(), streamObserver);
        });
    }

}
