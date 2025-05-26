package com.example.grpcclientdemo;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequest;
import org.springframework.security.oauth2.client.endpoint.RestClientClientCredentialsTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.util.function.Supplier;

/**
 * if servlet server is used, we can use threadLocal to store the tokens for a user
 * @return
 */
public class BlockingTokenSupplier implements Supplier<String> {
    ObjectProvider<ClientRegistrationRepository> context;

    public BlockingTokenSupplier(ObjectProvider<ClientRegistrationRepository> context) {
        this.context = context;
    }

    /**
     * provide a supplier to get called by interceptors each time It runs
     * TODO how to cache a token for a user(no users info found this place)
     */
    @Override
    public String get() {
        System.out.println("Thread: " + Thread.currentThread().getName());

        RestClientClientCredentialsTokenResponseClient creds = new RestClientClientCredentialsTokenResponseClient();
        ClientRegistrationRepository registry = context.getObject();
        ClientRegistration reg = registry.findByRegistrationId(StubConfiguration.GRPC_CLIENT_NAME);

        OAuth2AccessToken accessToken = creds.getTokenResponse(new OAuth2ClientCredentialsGrantRequest(reg))
                .getAccessToken();
        return accessToken.getTokenValue();
    }
}
