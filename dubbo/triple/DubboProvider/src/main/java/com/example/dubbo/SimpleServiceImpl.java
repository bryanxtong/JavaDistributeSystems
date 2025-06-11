package com.example.dubbo;

import org.apache.dubbo.common.stream.StreamObserver;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.grpc.sample.proto.DubboSimpleTriple;
import org.springframework.grpc.sample.proto.HelloRequest;
import org.springframework.grpc.sample.proto.HelloReply;
import java.util.concurrent.TimeUnit;

@DubboService
public class SimpleServiceImpl extends DubboSimpleTriple.SimpleImplBase {

    /**
     * Unary RPC: sayHello
     */
    @Override
    public HelloReply sayHello(HelloRequest request) {
        String message = "Hello, " + request.getName();
        HelloReply reply = HelloReply.newBuilder()
                .setMessage(message)
                .build();

        return reply;
    }

    /**
     * Server Streaming RPC: streamHello
     */
    @Override
    public void streamHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        try {
            for (int i = 1; i <= 5; i++) {
                String message = "Hello, " + request.getName() + " - Message " + i;
                HelloReply reply = HelloReply.newBuilder().setMessage(message).build();
                responseObserver.onNext(reply);
                TimeUnit.MILLISECONDS.sleep(100);
            }
        } catch (InterruptedException e) {
            responseObserver.onError(e);
        } finally {
            responseObserver.onCompleted();
        }
    }

    /**
     * Client Streaming RPC: clientStreamHello
     */
    @Override
    public StreamObserver<HelloRequest> clientStreamHello(StreamObserver<HelloReply> responseObserver) {
        return new StreamObserver<>() {
            StringBuilder names = new StringBuilder();

            @Override
            public void onNext(HelloRequest request) {
                if (names.length() > 0) {
                    names.append(", ");
                }
                names.append(request.getName());
            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onError(t);
            }

            @Override
            public void onCompleted() {
                String message = "Hello, All Users:" + names.toString();
                HelloReply reply = HelloReply.newBuilder().setMessage(message).build();
                responseObserver.onNext(reply);
                responseObserver.onCompleted();
            }
        };
    }

    /**
     * Bidirectional Streaming RPC: biStreamHello
     */
    @Override
    public StreamObserver<HelloRequest> biStreamHello(StreamObserver<HelloReply> responseObserver) {
        return new StreamObserver<HelloRequest>() {
            @Override
            public void onNext(HelloRequest request) {
                String message = " Hello," + request.getName();
                HelloReply reply = HelloReply.newBuilder().setMessage(message).build();
                responseObserver.onNext(reply);
            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onError(t);
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }
}