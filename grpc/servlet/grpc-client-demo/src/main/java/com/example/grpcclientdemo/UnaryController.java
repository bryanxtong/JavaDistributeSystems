package com.example.grpcclientdemo;

import io.grpc.StatusException;
import io.grpc.stub.BlockingClientCall;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.sample.proto.HelloReply;
import org.springframework.grpc.sample.proto.HelloRequest;
import org.springframework.grpc.sample.proto.SimpleGrpc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("api")
public class UnaryController {
    @Autowired
    SimpleGrpc.SimpleBlockingStub simpleBlockingStub;
    @Autowired
    SimpleGrpc.SimpleBlockingV2Stub simpleBlockingV2Stub;

    @GetMapping("/sayHello/{name}")
    public String unary(@PathVariable String name) {
        String message = "";
        try {
            message = simpleBlockingStub.sayHello(HelloRequest.newBuilder().setName(name).build()).getMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }

/*    @GetMapping("/blocking/{name}")
    public List<String> unary1(@PathVariable String name) throws StatusException, InterruptedException, TimeoutException {
        BlockingClientCall<?, HelloReply> blockingClientCall = simpleBlockingV2Stub.streamHello(HelloRequest.newBuilder().setName(name).build());
        List<String> response = new ArrayList<>();
        while (blockingClientCall.hasNext()) {
            HelloReply read = blockingClientCall.read(5000, TimeUnit.MILLISECONDS);
            System.out.println(read.getMessage());
            response.add(read.getMessage());
        }
        return response;
    }

    @GetMapping("/blocking1/{name}")
    public List<String> streamHello(@PathVariable String name) throws InterruptedException {
        List<String> response = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);
        StreamObserver<HelloReply> streamObserver = new StreamObserver<>() {
            @Override
            public void onNext(HelloReply helloReply) {
                response.add(helloReply.getMessage());
                System.out.println(helloReply.getMessage());
            }

            @Override
            public void onError(Throwable throwable) {
                System.err.println(throwable.getMessage());
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("completed");
                latch.countDown();
            }
        };
        simpleStub.streamHello(HelloRequest.newBuilder().setName(name).build(), streamObserver);
        latch.await();
        return response;
    }*/
}
