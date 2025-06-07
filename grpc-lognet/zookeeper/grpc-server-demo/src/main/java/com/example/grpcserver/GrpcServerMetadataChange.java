package com.example.grpcserver;
import org.lognet.springboot.grpc.context.GRpcServerInitializedEvent;
import org.springframework.cloud.zookeeper.serviceregistry.ZookeeperRegistration;
import org.springframework.cloud.zookeeper.serviceregistry.ZookeeperServiceRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
/**
 * Write the grpc.port value to the metadata so that client can know which port It connects to
 * and zero is supported.
 */
@Configuration
public class GrpcServerMetadataChange {

    private final ZookeeperRegistration registration;
    private final ZookeeperServiceRegistry registry;

    public GrpcServerMetadataChange(ZookeeperServiceRegistry registry,
                                    ZookeeperRegistration registration) {
        this.registration = registration;
        this.registry = registry;
    }

    @EventListener
    public void onGrpcServerInitialized(GRpcServerInitializedEvent event) {
        int grpcPort = event.getServer().getPort();
        registration.getMetadata().put("grpc.port", String.valueOf(grpcPort));
        registry.register(registration);
        System.out.printf("Updated gRPC metadata - port: %d%n", grpcPort);
    }
}
