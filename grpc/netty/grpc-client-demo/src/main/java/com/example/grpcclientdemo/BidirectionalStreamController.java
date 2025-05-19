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
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("api")
public class BidirectionalStreamController {
    @Autowired
    SimpleGrpc.SimpleStub simpleStub;

    /**
     * Bidirectional stream
     * curl -X POST http://192.168.71.116:8080/api/biStream -H "Content-Type: application/x-ndjson" --data-binary $'"Alice"\n"Bob"\n'
     */
    @PostMapping(value = "/biStream", consumes = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<String> biStreamHello(@RequestBody Flux<String> names) {
        return Flux.create(emitter -> {
            StreamObserver<HelloRequest> requestObserver = simpleStub.biStreamHello(new StreamObserver<>() {
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
