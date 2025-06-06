package org.lognet.springboot.gprc.autoconfigure.zookeeper;

import org.lognet.springboot.grpc.autoconfigure.GRpcServerProperties;
import org.lognet.springboot.grpc.context.GRpcServerInitializedEvent;
import org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryProperties;
import org.springframework.cloud.zookeeper.serviceregistry.ZookeeperRegistration;
import org.springframework.cloud.zookeeper.serviceregistry.ZookeeperServiceRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.event.EventListener;
import java.util.ArrayList;
import java.util.*;
import java.util.Objects;

public class GrpcZookeeperRegistar implements SmartLifecycle {
    public static final String GPPC_PORT = "grpc.port";
    private final ZookeeperServiceRegistry zookeeperServiceRegistry;
    private List<ZookeeperRegistration> registrations;

    public GrpcZookeeperRegistar(ZookeeperServiceRegistry zookeeperServiceRegistry) {
        this.zookeeperServiceRegistry = zookeeperServiceRegistry;
    }

    @EventListener
    public void onGrpcServerStarted(GRpcServerInitializedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        ZookeeperDiscoveryProperties zookeeperDiscoveryProperties = applicationContext.getBean(ZookeeperDiscoveryProperties.class);
        GrpcZookeeperProperties grpcZookeeperProperties = applicationContext.getBean(GrpcZookeeperProperties.class);
        GRpcServerProperties gRpcServerProperties = applicationContext.getBean(GRpcServerProperties.class);
        ZookeeperRegistration registration = applicationContext.getBean(ZookeeperRegistration.class);

        //metadata from spring.cloud.zookeeper.discovery and gprc.zookeeper.discovery will override as they are using the same instance
        registration.getMetadata().putAll(grpcZookeeperProperties.getDiscovery().getMetadata());
        registration.getMetadata().putAll(zookeeperDiscoveryProperties.getMetadata());
        Integer port = gRpcServerProperties.getPort();
        if (port == null || port == 0) {
            registration.getMetadata().put(GPPC_PORT, String.valueOf(gRpcServerProperties.getRunningPort()));
        } else {
            registration.getMetadata().put(GPPC_PORT, String.valueOf(gRpcServerProperties.getPort()));
        }
        System.out.println(registration.getMetadata());
        this.registrations = new ArrayList<>();
        this.registrations.add(registration);
        List<ZookeeperRegistration> var10000 = this.registrations;
        ZookeeperServiceRegistry var10001 = this.zookeeperServiceRegistry;
        Objects.requireNonNull(var10001);
        var10000.forEach(var10001::register);
    }

    @Override
    public boolean isAutoStartup() {
        return false;
    }

    @Override
    public void stop(Runnable callback) {
        this.stop();
        callback.run();
    }

    @Override
    public int getPhase() {
        return 0;
    }

    @Override
    public void start() {}

    @Override
    public void stop() {
        List<ZookeeperRegistration> var10000 = this.registrations;
        ZookeeperServiceRegistry var10001 = this.zookeeperServiceRegistry;
        Objects.requireNonNull(var10001);
        var10000.forEach(var10001::deregister);
        this.zookeeperServiceRegistry.close();
        this.registrations = null;
    }

    @Override
    public boolean isRunning() {
        return null != this.registrations;
    }
}
