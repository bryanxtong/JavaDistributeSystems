package com.example.dubbo;

import com.alibaba.csp.sentinel.adapter.dubbo3.config.DubboAdapterGlobalConfig;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDubbo
public class DubboConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DubboConsumerApplication.class, args);
    }

    @Bean
    public DubboConsumerFallBack sentinelConsumerFallback(){
        DubboConsumerFallBack dubboConsumerFallBack = new DubboConsumerFallBack();
        DubboAdapterGlobalConfig.setConsumerFallback(dubboConsumerFallBack);
        return dubboConsumerFallBack;
    }

}
