package com.example.grpcclient;

import io.grpc.stub.StreamObserver;
import org.lognet.springboot.grpc.security.AuthCallCredentials;
import org.lognet.springboot.grpc.security.AuthHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.sample.proto.HelloReply;
import org.springframework.grpc.sample.proto.HelloRequest;
import org.springframework.grpc.sample.proto.SimpleGrpc;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.util.List;

@RestController
@RequestMapping("api")
public class ClientStreamController extends BaseController {
    @Autowired
    SimpleGrpc.SimpleStub simpleStub;

    /**
     * Client stream and only one reply
     * on linux or postman
     * curl -X POST http://localhost:8080/api/clientStream -H "Content-Type: application/json" -d '["Alice", "Bob", "Charlie"]'
     */
    @PostMapping(value = "/clientStream", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> clientStream(@RequestBody List<String> names,@RequestHeader("Authorization") String authHeader) {
        String token = this.extractToken(authHeader);
        AuthCallCredentials callCredentials = new AuthCallCredentials(
                AuthHeader.builder().bearer().tokenSupplier(()-> ByteBuffer.wrap(token.getBytes())).build()
        );
        return Mono.create(emitter -> {
            StreamObserver<HelloRequest> requestObserver = simpleStub
                    .withCallCredentials(callCredentials)
                    .clientStreamHello(new StreamObserver<>() {
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
