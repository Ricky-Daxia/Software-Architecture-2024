package org.example.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class FluxProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(FluxProductApplication.class, args);
    }
}
