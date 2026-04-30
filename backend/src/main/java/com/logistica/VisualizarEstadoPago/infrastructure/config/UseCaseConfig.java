package com.logistica.VisualizarEstadoPago.infrastructure.config;

import com.logistica.VisualizarEstadoPago.application.usecases.pago.ConsultarEstadoPagoUseCase;
import com.logistica.VisualizarEstadoPago.domain.repositories.PagoRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public ConsultarEstadoPagoUseCase consultarEstadoPagoUseCase(PagoRepository pagoRepository) {
        return new ConsultarEstadoPagoUseCase(pagoRepository);
    }
}
