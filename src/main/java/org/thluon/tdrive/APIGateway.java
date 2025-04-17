package org.thluon.tdrive;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class APIGateway {
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(APIGateway.class, args);
    }
}
