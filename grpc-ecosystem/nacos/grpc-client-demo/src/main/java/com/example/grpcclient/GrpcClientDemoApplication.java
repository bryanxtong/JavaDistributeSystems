package com.example.grpcclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GrpcClientDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrpcClientDemoApplication.class, args);
    }

}
