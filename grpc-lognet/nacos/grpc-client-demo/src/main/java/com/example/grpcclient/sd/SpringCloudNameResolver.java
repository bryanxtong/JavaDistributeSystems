package com.example.grpcclient.sd;

import com.google.common.collect.ImmutableMap;
import io.grpc.EquivalentAddressGroup;
import io.grpc.NameResolver;
import io.grpc.Status;
import io.grpc.SynchronizationContext;
import io.grpc.internal.SharedResourceHolder;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class SpringCloudNameResolver extends NameResolver{
    private static final String GPRC_PORT = "grpc.port";
    private Listener2 listener;
    private final URI uri;
    private DiscoveryClient discoveryClient;
    private SharedResourceHolder.Resource<ScheduledExecutorService> executorResource;
    private ScheduledExecutorService scheduledExecutorService;
    private final SynchronizationContext syncContext;

    public SpringCloudNameResolver(URI uri, DiscoveryClient discoveryClient, NameResolver.Args args, SharedResourceHolder.Resource<ScheduledExecutorService> executorResource) {
        this.uri = uri;
        this.discoveryClient = discoveryClient;
        //which instances should be executed in syncContext
        this.syncContext = requireNonNull(args.getSynchronizationContext(), "syncContext");
        this.executorResource = executorResource;
        //refresh the server list every 5 seconds
        this.scheduledExecutorService = SharedResourceHolder.get(executorResource);
    }

    @Override
    public String getServiceAuthority() {
        if (uri.getHost() != null) {
            return uri.getHost();
        }
        return "";
    }

    @Override
    public void shutdown() {
        if(null != scheduledExecutorService){
            SharedResourceHolder.release(this.executorResource, this.scheduledExecutorService);
        }
        this.listener = null;
    }

    @Override
    public void refresh() {
        this.syncContext.execute(() -> {
            resolve();
        });
    }

    private void resolve() {
        List<ServiceInstance> instances = discoveryClient.getInstances(StubConfiguration.serviceName);
        Map<String, List<InetSocketAddress>> addrStore = ImmutableMap.<String, List<InetSocketAddress>>builder().put(StubConfiguration.serviceName, instances
                        .stream()
                        .map(serviceInstance -> new InetSocketAddress(serviceInstance.getHost(), Integer.valueOf(serviceInstance.getMetadata().getOrDefault(GPRC_PORT, "0"))))
                        .collect(Collectors.toList()))
                .build();
        List<InetSocketAddress> addresses = addrStore.get(uri.getPath().substring(1));
        try {
            List<EquivalentAddressGroup> equivalentAddressGroups = addresses.stream()
                    .map(this::toSocketAddress)
                    .map(Arrays::asList)
                    .map(this::addrToEquivalentAddressGroup)
                    .collect(Collectors.toList());
            ResolutionResult resolutionResult = ResolutionResult.newBuilder().setAddresses(equivalentAddressGroups).build();
            this.listener.onResult(resolutionResult);
        } catch (Exception e) {
            this.listener.onError(Status.UNAVAILABLE.withDescription("Unable to resolve host ").withCause(e));
        }
    }

    @Override
    public void start(Listener2 listener) {
        this.listener = listener;
        this.syncContext.execute(() -> {
            resolve();
            scheduledExecutorService.scheduleAtFixedRate(SpringCloudNameResolver.this::resolve, 0,5, TimeUnit.SECONDS);
        });
    }

    private SocketAddress toSocketAddress(InetSocketAddress address) {
        return new InetSocketAddress(address.getAddress(), address.getPort());
    }

    private EquivalentAddressGroup addrToEquivalentAddressGroup(List<SocketAddress> socketAddresses) {
        return new EquivalentAddressGroup(socketAddresses);
    }
}
