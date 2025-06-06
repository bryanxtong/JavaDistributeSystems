package com.example.grpcclient.sd;

import io.grpc.NameResolver;
import io.grpc.NameResolverProvider;
import io.grpc.internal.GrpcUtil;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import java.net.URI;

public class SpringCloudNameResolverProvider extends NameResolverProvider {
    final DiscoveryClient discoveryClient;

    public SpringCloudNameResolverProvider(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @Override
    protected boolean isAvailable() {
        return true;
    }

    @Override
    protected int priority() {
        return 5;
    }

    @Override
    public NameResolver newNameResolver(URI targetUri,NameResolver.Args args) {
        return new SpringCloudNameResolver(targetUri, discoveryClient, args, GrpcUtil.TIMER_SERVICE);
    }

    @Override
    public String getDefaultScheme() {
        return "grpc";
    }
}
