package com.example.grpcserverdemo;

import io.grpc.stub.StreamObserver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.grpc.sample.proto.HelloReply;
import org.springframework.grpc.sample.proto.HelloRequest;
import org.springframework.grpc.sample.proto.SimpleGrpc;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class GrpcServerService extends SimpleGrpc.SimpleImplBase {
    private Log log = LogFactory.getLog(GrpcServerService.class);

    public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
        HelloReply reply = HelloReply.newBuilder().setMessage("Hello=>" + req.getName()).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    public void streamHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
        int count = 0;
        while (count < 10) {
            HelloReply reply = HelloReply.newBuilder().setMessage("Hello(" + count + ")===>" + req.getName()).build();
            responseObserver.onNext(reply);
            count++;
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                responseObserver.onError(e);
                return;
            }
        }
        responseObserver.onCompleted();
    }

    public StreamObserver<HelloRequest> clientStreamHello(StreamObserver<HelloReply> responseObserver) {
        return new StreamObserver<>() {
            StringBuilder names = new StringBuilder();
            @Override
            public void onNext(HelloRequest helloRequest) {
                names.append(helloRequest.getName()).append(",");
            }
            @Override
            public void onError(Throwable throwable) {
                responseObserver.onError(throwable);
            }
            @Override
            public void onCompleted() {
                String message = names.substring(0, names.length() - 1);
                responseObserver.onNext(HelloReply.newBuilder().setMessage("Hello all clients: " + message).build());
                responseObserver.onCompleted();
            }
        };
    }

    public StreamObserver<HelloRequest> biStreamHello(StreamObserver<HelloReply> responseObserver) {
        return new StreamObserver<>() {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            @Override
            public void onNext(HelloRequest helloRequest) {
                responseObserver.onNext(HelloReply.newBuilder().setMessage(" Hello " + helloRequest.getName() + " at " + simpleDateFormat.format(new Date())).build());
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            @Override
            public void onError(Throwable throwable) {
                responseObserver.onError(throwable);
            }
            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }
}
