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
import reactor.core.publisher.Flux;

import java.nio.ByteBuffer;

@RestController
@RequestMapping("api")
public class BidirectionalStreamController extends BaseController {
    @Autowired
    SimpleGrpc.SimpleStub simpleStub;

    /**
     * Bidirectional stream
     * on linux or postman
     * curl -X POST http://localhost:8080/api/biStream -H "Content-Type: application/x-ndjson" --data-binary $'"Alice"\n"Bob"\n'
     */
    @PostMapping(value = "/biStream", consumes = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<String> biStreamHello(@RequestBody Flux<String> names, @RequestHeader("Authorization") String authHeader) {
        String token = this.extractToken(authHeader);
        AuthCallCredentials callCredentials = new AuthCallCredentials(
                AuthHeader.builder().bearer().tokenSupplier(()-> ByteBuffer.wrap(token.getBytes())).build()
        );
        return Flux.create(emitter -> {
            StreamObserver<HelloRequest> requestObserver = simpleStub
                    .withCallCredentials(callCredentials)
                    .biStreamHello(new StreamObserver<>() {
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
