package com.example.grpcclient;

import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.client.security.CallCredentialsHelper;
import org.springframework.grpc.sample.proto.HelloRequest;
import org.springframework.grpc.sample.proto.SimpleGrpc;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class UnaryController extends BaseController{
    @GrpcClient("gprc-server-demo")
    SimpleGrpc.SimpleBlockingStub simpleBlockingStub;

    @GetMapping("/unary/{name}")
    public String unary(@PathVariable String name, @RegisteredOAuth2AuthorizedClient("grpc-client") OAuth2AuthorizedClient authorizedClient) {
        String token = this.getAccessToken(authorizedClient);
        try{
            return simpleBlockingStub
                    .withCallCredentials(CallCredentialsHelper.bearerAuth(token))
                    .sayHello(HelloRequest.newBuilder().setName(name).build()).getMessage();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
