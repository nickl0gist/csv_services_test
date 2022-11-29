package com.horkovtest.owner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class OwnerApplication {
    public static void main(String[] args) {
        SpringApplication.run(OwnerApplication.class, args);
    }
}
