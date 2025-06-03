package com.example.grpcclient;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.client.security.CallCredentialsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.sample.proto.HelloReply;
import org.springframework.grpc.sample.proto.HelloRequest;
import org.springframework.grpc.sample.proto.SimpleGrpc;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("api")
public class ClientStreamController extends BaseController {
    @GrpcClient("gprc-server-demo")
    SimpleGrpc.SimpleStub simpleStub;

    /**
     * Client stream and only one reply
     * on linux or postman
     * curl -X POST http://localhost:8080/api/clientStream -H "Content-Type: application/json" -d '["Alice", "Bob", "Charlie"]'
     */
    @PostMapping(value = "/clientStream", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> clientStream(@RequestBody List<String> names,@RequestHeader("Authorization") String authHeader) {
        String token = this.extractToken(authHeader);
        System.out.println("token: " + token);
        return Mono.create(emitter -> {
            StreamObserver<HelloRequest> requestObserver = simpleStub
                    .withCallCredentials(CallCredentialsHelper.bearerAuth(token))
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
