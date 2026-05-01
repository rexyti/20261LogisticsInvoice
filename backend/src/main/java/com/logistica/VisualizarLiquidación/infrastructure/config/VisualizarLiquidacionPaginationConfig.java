package com.logistica.VisualizarLiquidación.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@Configuration
@EnableSpringDataWebSupport
public class VisualizarLiquidacionPaginationConfig {
    // La propiedad pageSerializationMode solo está disponible a partir de Spring Boot 3.3+ (Spring Data 3.3+)
    // Como el proyecto usa Spring Boot 3.2.4, se utiliza la configuración por defecto.
}
