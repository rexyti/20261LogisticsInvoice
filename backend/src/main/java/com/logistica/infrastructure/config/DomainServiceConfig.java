package com.logistica.infrastructure.config;

import com.logistica.domain.services.EstadoPaqueteService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainServiceConfig {

    @Bean
    public EstadoPaqueteService estadoPaqueteService() {
        return new EstadoPaqueteService();
    }
}
