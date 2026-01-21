package com.bookstore.authorsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class AuthorsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthorsServiceApplication.class, args);
    }

}
