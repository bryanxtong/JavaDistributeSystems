package org.lognet.springboot.gprc.autoconfigure.zookeeper;

import org.lognet.springboot.grpc.GRpcServerRunner;
import org.lognet.springboot.grpc.autoconfigure.GRpcAutoConfiguration;
import org.lognet.springboot.grpc.autoconfigure.OnGrpcServerEnabled;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindHandlerAdvisor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.AbstractBindHandler;
import org.springframework.boot.context.properties.bind.BindContext;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryProperties;
import org.springframework.cloud.zookeeper.serviceregistry.ZookeeperServiceRegistry;
import org.springframework.cloud.zookeeper.serviceregistry.ZookeeperServiceRegistryAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
@ConditionalOnClass({ZookeeperServiceRegistry.class})
@AutoConfigureAfter({ZookeeperServiceRegistryAutoConfiguration.class, GRpcAutoConfiguration.class})
@ConditionalOnProperty(value = {"spring.cloud.service-registry.auto-registration.enabled"}, matchIfMissing = true)
@ConditionalOnBean({ZookeeperServiceRegistry.class, GRpcServerRunner.class})
@OnGrpcServerEnabled
@EnableConfigurationProperties({GrpcZookeeperProperties.class})
public class ZookeeperGrpcAutoConfiguration {

    @Bean
    public ConfigurationPropertiesBindHandlerAdvisor configurationPropertiesBindHandlerAdvisor() {
        return bindHandler -> new AbstractBindHandler(bindHandler) {
            private final ConfigurationPropertyName gprcConfigName = ConfigurationPropertyName.of("grpc.zookeeper");
            @Override
            public <T> Bindable<T> onStart(ConfigurationPropertyName name, Bindable<T> target, BindContext context) {
                if (gprcConfigName.equals(name)) {
                    ZookeeperDiscoveryProperties result = context.getBinder().bindOrCreate("spring.cloud.zookeeper.discovery", ZookeeperDiscoveryProperties.class);
                    GrpcZookeeperProperties p = (GrpcZookeeperProperties) target.getValue().get();
                    p.setDiscovery(result);
                }
                return super.onStart(name, target, context);
            }
        };
    }

    @Bean
    public GrpcZookeeperRegistar grpcZookeeperRegistar(ZookeeperServiceRegistry zookeeperServiceRegistry) {
        return new GrpcZookeeperRegistar(zookeeperServiceRegistry);
    }
}
