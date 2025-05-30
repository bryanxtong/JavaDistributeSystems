package com.example.grpcclient;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class TokenController extends BaseController{

    @GetMapping("/token")
    public String token(@RegisteredOAuth2AuthorizedClient("grpc-client") OAuth2AuthorizedClient authorizedClient){
        return this.getAccessToken(authorizedClient);
    }
}
