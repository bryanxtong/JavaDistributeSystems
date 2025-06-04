package com.example.grpcserver;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.grpc.sample.proto.HelloReply;
import org.springframework.grpc.sample.proto.HelloRequest;
import org.springframework.grpc.sample.proto.SimpleGrpc;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@GrpcService
public class GrpcServerService extends SimpleGrpc.SimpleImplBase {
    private Log log = LogFactory.getLog(GrpcServerService.class);
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
        HelloReply reply = HelloReply.newBuilder().setMessage("Hello=>" + req.getName()).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    public void streamHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
        try {
            for (int i = 0; i < 10; i++) {
                HelloReply reply = HelloReply.newBuilder()
                        .setMessage("Hello(" + i + ")===>" + req.getName())
                        .build();
                responseObserver.onNext(reply);
            }
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
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
            @Override
            public void onNext(HelloRequest helloRequest) {
                responseObserver.onNext(HelloReply.newBuilder().setMessage(" Hello " + helloRequest.getName() + " at " + LocalDateTime.now().format(formatter)).build());
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
