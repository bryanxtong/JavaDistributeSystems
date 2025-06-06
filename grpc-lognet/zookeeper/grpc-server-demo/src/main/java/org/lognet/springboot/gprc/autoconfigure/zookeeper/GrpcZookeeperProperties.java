package org.lognet.springboot.gprc.autoconfigure.zookeeper;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryProperties;

@ConfigurationProperties(prefix = "grpc.zookeeper")
public class GrpcZookeeperProperties {

    @NestedConfigurationProperty
    private ZookeeperDiscoveryProperties discovery;

    public ZookeeperDiscoveryProperties getDiscovery() {
        return discovery;
    }

    public void setDiscovery(final ZookeeperDiscoveryProperties discovery) {
        this.discovery = discovery;
    }
}
