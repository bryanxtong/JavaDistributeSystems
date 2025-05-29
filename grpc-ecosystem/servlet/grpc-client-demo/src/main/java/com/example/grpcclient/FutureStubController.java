package com.example.grpcclient;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.client.security.CallCredentialsHelper;
import org.springframework.grpc.sample.proto.HelloReply;
import org.springframework.grpc.sample.proto.HelloRequest;
import org.springframework.grpc.sample.proto.SimpleGrpc;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
@RestController
@RequestMapping("api")
public class FutureStubController extends BaseController {
    @GrpcClient("local-grpc-server")
    SimpleGrpc.SimpleFutureStub simpleFutureStub;

    @GetMapping("/async/{name}")
    public Future<String> future(@PathVariable String name,@RegisteredOAuth2AuthorizedClient("grpc-client") OAuth2AuthorizedClient authorizedClient) {
        String token = this.getAccessToken(authorizedClient);
        ListenableFuture<HelloReply> grpcFuture = simpleFutureStub
                .withCallCredentials(CallCredentialsHelper.bearerAuth(token))
                .sayHello(HelloRequest.newBuilder().setName(name).build());
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

    @GetMapping("/async1/{name}")
    public Mono<String> future1(@PathVariable String name,@RegisteredOAuth2AuthorizedClient("grpc-client") OAuth2AuthorizedClient authorizedClient) {
        String token = this.getAccessToken(authorizedClient);
        ListenableFuture<HelloReply> grpcFuture = simpleFutureStub
                //.withDeadlineAfter(5, TimeUnit.SECONDS)
                .withCallCredentials(CallCredentialsHelper.bearerAuth(token))
                .sayHello(HelloRequest.newBuilder().setName(name).build());
        return Mono.create((Consumer<MonoSink<HelloReply>>) monoSink -> Futures.addCallback(grpcFuture, new FutureCallback<>() {
                    @Override
                    public void onSuccess(HelloReply result) {
                        monoSink.success(result);
                    }
                    @Override
                    public void onFailure(Throwable t) {
                        monoSink.error(t);
                    }
                }, MoreExecutors.directExecutor()))
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(TimeoutException.class, e -> Mono.error(new RuntimeException("gRPC call timeout")))
                .retryWhen(Retry.backoff(3, Duration.ofMillis(100)))
                .map(helloReply -> helloReply.getMessage());
    }
}
