package com.example.grpcclientdemo;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.sample.proto.HelloReply;
import org.springframework.grpc.sample.proto.HelloRequest;
import org.springframework.grpc.sample.proto.SimpleGrpc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

@RestController
@RequestMapping("api")
public class FutureStubController {
    @Autowired
    SimpleGrpc.SimpleFutureStub simpleFutureStub;

    @GetMapping("/async/{name}")
    public Future<String> future(@PathVariable String name) {
        ListenableFuture<HelloReply> grpcFuture = simpleFutureStub.sayHello(HelloRequest.newBuilder().setName(name).build());
        CompletableFuture<HelloReply> completableFuture = new CompletableFuture<>();
        Futures.addCallback(grpcFuture, new FutureCallback<>() {
            @Override
            public void onSuccess(HelloReply result) {
                completableFuture.complete(result);
            }
            @Override
            public void onFailure(Throwable t) {
                completableFuture.completeExceptionally(t);
            }
        }, ForkJoinPool.commonPool());
        return completableFuture.thenApplyAsync(helloReply -> helloReply.getMessage());

    }
}
