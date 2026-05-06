package com.logistica.infrastructure.registrarEstadoPago.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.logistica.infrastructure.persistence.repositories")
public class JpaConfig {
}
