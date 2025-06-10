package com.example.dubbo;

import com.alibaba.csp.sentinel.adapter.dubbo3.config.DubboAdapterGlobalConfig;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDubbo
public class DubboProviderApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DubboProviderApplication.class, args);
    }

    @Bean
    public DubboProviderFallBack sentinelProviderFallback(){
        DubboProviderFallBack dubboProviderFallBack = new DubboProviderFallBack();
        DubboAdapterGlobalConfig.setProviderFallback(dubboProviderFallBack);
        return dubboProviderFallBack;
    }

}
