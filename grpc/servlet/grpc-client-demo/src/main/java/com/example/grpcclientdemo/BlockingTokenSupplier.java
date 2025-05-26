package com.example.grpcclientdemo;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequest;
import org.springframework.security.oauth2.client.endpoint.RestClientClientCredentialsTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * if servlet server is used, we can use threadLocal to store the tokens for a user
 * @return
 */
public class BlockingTokenSupplier implements Supplier<String> {
    ObjectProvider<ClientRegistrationRepository> context;
    ConcurrentHashMap<String, TokenInfo> tokensMap = new ConcurrentHashMap<>();

    public BlockingTokenSupplier(ObjectProvider<ClientRegistrationRepository> context) {
        this.context = context;
    }

    /**
     * provide a supplier to get called by interceptors each time It runs
     */
    @Override
    public String get() {
        System.out.println("Thread: " + Thread.currentThread().getName());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found");
        }
        Instant now = Instant.now();
        Object principal = authentication.getPrincipal();
        String username = "";
        if(principal instanceof UserDetails userDetails) {
            username = userDetails.getUsername();
        }

        TokenInfo tokenInfo = tokensMap.get(username);
        if(authentication.isAuthenticated() && tokenInfo != null && tokenInfo.getExpiredAt().isAfter(now)) {
            return tokenInfo.getToken();
        }

        RestClientClientCredentialsTokenResponseClient creds = new RestClientClientCredentialsTokenResponseClient();
        ClientRegistrationRepository registry = context.getObject();
        ClientRegistration reg = registry.findByRegistrationId(StubConfiguration.GRPC_CLIENT_NAME);

        OAuth2AccessToken accessToken = creds.getTokenResponse(new OAuth2ClientCredentialsGrantRequest(reg))
                .getAccessToken();

        tokensMap.put(username, new TokenInfo(accessToken.getTokenValue(), accessToken.getExpiresAt()));
        //System.out.println("Thread: " + Thread.currentThread().getName() + accessToken.getTokenValue());
        return accessToken.getTokenValue();
    }

    static class TokenInfo{
        private String token;
        private Instant expiredAt;

        public TokenInfo(String token, Instant expiredAt) {
            this.token = token;
            this.expiredAt = expiredAt;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public Instant getExpiredAt() {
            return expiredAt;
        }

        public void setExpiredAt(Instant expiredAt) {
            this.expiredAt = expiredAt;
        }
    }
}
