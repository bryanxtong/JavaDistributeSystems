package com.example.grpcclientdemo;

import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequest;
import org.springframework.security.oauth2.client.endpoint.WebClientReactiveClientCredentialsTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * BearerTokenAuthenticationInterceptor needs a token before It can continue to process
 * This class we mixed the sync method and async method to get the tokens, It is not good
 * we use this as DefaultRestClient has block exceptions during webflux app
 */
public class BlockingTokenSupplier implements Supplier<String> {

    private ClientRegistration reg;
    private WebClientReactiveClientCredentialsTokenResponseClient creds;

    public BlockingTokenSupplier(ClientRegistration reg, WebClientReactiveClientCredentialsTokenResponseClient creds) {
        this.reg = reg;
        this.creds = creds;
    }

    /**
     * Each time, this method to be called by BearerTokenAuthenticationInterceptor to get token. TODO
     * @return
     */
    @Override
    public String get() {
        System.out.println("Supplier Thread: " + Thread.currentThread().getName());
        OAuth2AccessToken oAuth2AccessToken = null;
        try {
            oAuth2AccessToken = creds.getTokenResponse(new OAuth2ClientCredentialsGrantRequest(reg)).subscribeOn(Schedulers.boundedElastic()) //add subscribeOn(Schedulers.boundedElastic() to eliminate blocking
                    .flatMap(oAuth2AccessTokenResponse -> Mono.justOrEmpty(oAuth2AccessTokenResponse.getAccessToken()))
                    .toFuture()
                    .get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        return oAuth2AccessToken.getTokenValue();
    }
}
