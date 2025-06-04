package com.example.grpcclient;
import org.lognet.springboot.grpc.security.AuthCallCredentials;
import org.lognet.springboot.grpc.security.AuthHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.sample.proto.HelloRequest;
import org.springframework.grpc.sample.proto.SimpleGrpc;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.nio.ByteBuffer;

@RestController
@RequestMapping("api")
public class UnaryController extends BaseController{
    @Autowired
    SimpleGrpc.SimpleBlockingStub simpleBlockingStub;

    @GetMapping("/unary/{name}")
    public String unary(@PathVariable String name, @RegisteredOAuth2AuthorizedClient("grpc-client") OAuth2AuthorizedClient authorizedClient) {
        String token = this.getAccessToken(authorizedClient);
        AuthCallCredentials callCredentials = new AuthCallCredentials(
                AuthHeader.builder().bearer().tokenSupplier(()-> ByteBuffer.wrap(token.getBytes())).build()
        );
        try{
            return simpleBlockingStub
                    .withCallCredentials(callCredentials)
                    .sayHello(HelloRequest.newBuilder().setName(name).build()).getMessage();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
