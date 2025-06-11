package com.example.dubbo;

import org.apache.dubbo.common.stream.StreamObserver;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.grpc.sample.proto.HelloReply;
import org.springframework.grpc.sample.proto.HelloRequest;
import org.springframework.grpc.sample.proto.Simple;
import org.springframework.stereotype.Service;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Service
public class SimpleServiceClient {

    @DubboReference(loadbalance = "roundrobin")
    private Simple simpleService;

    public void sayHello(String name, Consumer<String> responseConsumer, Consumer<Throwable> errorConsumer) {
        try {
            HelloReply helloReply = simpleService.sayHello(
                    HelloRequest.newBuilder()
                            .setName(name)
                            .build());
            responseConsumer.accept(helloReply.getMessage());
        } catch (Exception e) {
            errorConsumer.accept(e);
        }
    }

    /**
     * Server Streaming RPC: streamHello
     */
    public void streamHello(String name, Consumer<String> onNext, Runnable onCompleted, Consumer<Throwable> onError) {
        simpleService.streamHello(
                HelloRequest.newBuilder()
                        .setName(name)
                        .build(),
                new StreamObserver<>() {
                    @Override
                    public void onNext(HelloReply reply) {
                        onNext.accept(reply.getMessage());
                    }

                    @Override
                    public void onError(Throwable t) {
                        onError.accept(t);
                    }

                    @Override
                    public void onCompleted() {
                        onCompleted.run();
                    }
                }
        );
    }

    /**
     * Client Streaming RPC: clientStreamHello
     */
    public void clientStreamHello(Consumer<StreamObserver<HelloRequest>> requestObserverHandler, Consumer<String> completeHandler, Consumer<Throwable> onError) {
        StreamObserver<HelloRequest> requestObserver =
                simpleService.clientStreamHello(new StreamObserver<>() {
                    @Override
                    public void onNext(HelloReply reply) {
                        completeHandler.accept(reply.getMessage());
                    }

                    @Override
                    public void onError(Throwable t) {
                        onError.accept(t);
                    }

                    @Override
                    public void onCompleted() {
                        System.out.println("Client streaming completed");
                    }
                });

        requestObserverHandler.accept(requestObserver);

    }

    public void biStreamHello(
            Consumer<StreamObserver<HelloRequest>> requestHandler,
            BiConsumer<StreamObserver<HelloReply>, HelloReply> onNextHandler,
            Consumer<Throwable> errorHandler,
            Runnable completeHandler
    ) {
        StreamObserver<HelloReply> serverResponseObserver = new StreamObserver<>() {
            @Override
            public void onNext(HelloReply reply) {
                onNextHandler.accept(this, reply);
            }

            @Override
            public void onError(Throwable t) {
                errorHandler.accept(t);
            }

            @Override
            public void onCompleted() {
                completeHandler.run();
            }
        };

        StreamObserver<HelloRequest> requestObserver = simpleService.biStreamHello(serverResponseObserver);
        requestHandler.accept(requestObserver);
    }
}