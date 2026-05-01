package com.logistica;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.logistica")
@EntityScan(basePackages = "com.logistica")
@EnableFeignClients(basePackages = "com.logistica")
public class LogisticaApplication {
    public static void main(String[] args) {
        SpringApplication.run(LogisticaApplication.class, args);
    }
}