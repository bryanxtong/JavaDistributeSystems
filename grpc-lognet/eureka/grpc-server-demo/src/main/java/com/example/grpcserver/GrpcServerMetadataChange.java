package com.example.grpcserver;
import com.netflix.discovery.EurekaClient;
import org.lognet.springboot.grpc.context.GRpcServerInitializedEvent;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaRegistration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import java.util.HashMap;
import java.util.Map;

/**
 * Write the grpc.port value to the metadata so that client can know which port It connects to
 * and zero is supported.
 */
@Configuration
public class GrpcServerMetadataChange {

    private final EurekaRegistration registration;
    private final EurekaClient eurekaClient;

    public GrpcServerMetadataChange(EurekaRegistration registration,
                                    EurekaClient eurekaClient) {
        this.registration = registration;
        this.eurekaClient = eurekaClient;
    }

    @EventListener
    public void onGrpcServerInitialized(GRpcServerInitializedEvent event) {
        int grpcPort = event.getServer().getPort();
        Map<String, String> metadata = new HashMap<>(registration.getMetadata());
        metadata.put("grpc.port", String.valueOf(grpcPort));
        eurekaClient.getApplicationInfoManager().registerAppMetadata(metadata);
        System.out.printf("Updated gRPC metadata - port: %d%n", grpcPort);
    }
}
