package com.example.grpcserver;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.cloud.nacos.registry.NacosRegistration;
import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.lognet.springboot.grpc.context.GRpcServerInitializedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Write the grpc.port value to the metadata so that client can know which port It connects to
 * and zero is supported.
 */
@Configuration
public class GrpcServerMetadataChange {

    @Value("${spring.application.name}")
    private String serviceName;

    private final NacosRegistration registration;
    private final NacosServiceManager nacosServiceManager;

    public GrpcServerMetadataChange(NacosRegistration registration,
                                    NacosServiceManager nacosServiceManager) {
        this.registration = registration;
        this.nacosServiceManager = nacosServiceManager;
    }

    @EventListener
    public void onGrpcServerInitialized(GRpcServerInitializedEvent event) throws NacosException {
        int grpcPort = event.getServer().getPort();
        Map<String, String> metadata = new ConcurrentHashMap<>(registration.getMetadata());
        metadata.put("grpc.port", String.valueOf(grpcPort));
        NamingService namingService = nacosServiceManager.getNamingService();
        Instance instance = new Instance();
        instance.setIp(registration.getHost());
        instance.setPort(registration.getPort());
        instance.setMetadata(metadata);
        instance.setWeight(1.0);
        instance.setClusterName(registration.getCluster());
        namingService.registerInstance(serviceName, Constants.DEFAULT_GROUP, instance);
        System.out.printf("Updated gRPC metadata - port: %d%n", grpcPort);
    }
}
