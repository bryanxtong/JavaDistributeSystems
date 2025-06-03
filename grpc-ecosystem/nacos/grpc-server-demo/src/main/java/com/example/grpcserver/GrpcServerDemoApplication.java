package com.example.grpcserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GrpcServerDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrpcServerDemoApplication.class, args);
    }

}
