package com.example.grpcclientdemo;

import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.sample.proto.HelloReply;
import org.springframework.grpc.sample.proto.HelloRequest;
import org.springframework.grpc.sample.proto.SimpleGrpc;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("api")
public class ClientStreamController {
    @Autowired
    SimpleGrpc.SimpleStub simpleStub;

    /**
     * Client stream and only one reply
     * curl -X POST http://192.168.71.116:8080/api/clientStream -H "Content-Type: application/json" -d '["Alice", "Bob", "Charlie"]'
     */
    @PostMapping(value = "/clientStream", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> clientStream(@RequestBody List<String> names) {
        return Mono.create(emitter -> {
            StreamObserver<HelloRequest> requestObserver = simpleStub.clientStreamHello(new StreamObserver<>() {
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
}
