package com.logistica.infrastructure.conductor.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ConductorRestClientConfig {

    @Value("${app.servicios.conductores.base-url}")
    private String baseUrl;

    @Bean("conductorRestClient")
    public RestClient conductorRestClient() {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
