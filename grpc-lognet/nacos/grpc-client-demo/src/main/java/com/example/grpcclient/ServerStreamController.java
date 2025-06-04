package com.example.grpcclient;

import io.grpc.stub.StreamObserver;
import org.lognet.springboot.grpc.security.AuthCallCredentials;
import org.lognet.springboot.grpc.security.AuthHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.sample.proto.HelloReply;
import org.springframework.grpc.sample.proto.HelloRequest;
import org.springframework.grpc.sample.proto.SimpleGrpc;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.nio.ByteBuffer;

@RestController
@RequestMapping("api")
public class ServerStreamController extends BaseController{
    @Autowired
    SimpleGrpc.SimpleStub simpleStub;

    /**
     * Server Stream
     */
    @GetMapping("/serverStream/{name}")
    public Flux<String> serverStream(@PathVariable String name,@RegisteredOAuth2AuthorizedClient("grpc-client") OAuth2AuthorizedClient authorizedClient) {
        String token = this.getAccessToken(authorizedClient);
        AuthCallCredentials callCredentials = new AuthCallCredentials(
                AuthHeader.builder().bearer().tokenSupplier(()-> ByteBuffer.wrap(token.getBytes())).build()
        );
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
            simpleStub.withCallCredentials(callCredentials).streamHello(HelloRequest.newBuilder().setName(name).build(), streamObserver);
        });
    }

}
