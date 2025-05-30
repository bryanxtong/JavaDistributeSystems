package com.example.grpcclient;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;

public class BaseController {
    /**
     * get an access token
     * @param authorizedClient
     * @return
     */
    public String getAccessToken(OAuth2AuthorizedClient authorizedClient){
        if (authorizedClient == null || authorizedClient.getAccessToken() == null) {
            throw new RuntimeException("Access token is missing");
        }
        String token = authorizedClient.getAccessToken().getTokenValue();
        return token;
    }

    /**
     * Extract header from request header
     * @param header
     * @return
     */
    protected String extractToken(String header) {
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        throw new IllegalArgumentException("Missing or invalid Authorization header");
    }
}
