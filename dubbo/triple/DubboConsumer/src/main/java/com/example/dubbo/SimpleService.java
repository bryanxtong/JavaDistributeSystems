package com.example.dubbo;

import org.apache.dubbo.common.stream.StreamObserver;
import org.springframework.grpc.sample.proto.HelloReply;
import org.springframework.grpc.sample.proto.HelloRequest;

public interface SimpleService {

    HelloReply sayHello(HelloRequest request);

    void streamHello(HelloRequest request, StreamObserver<HelloReply> responseObserver);

    StreamObserver<HelloRequest> clientStreamHello(StreamObserver<HelloReply> responseObserver);

    StreamObserver<HelloRequest> biStreamHello(StreamObserver<HelloReply> responseObserver);
}
