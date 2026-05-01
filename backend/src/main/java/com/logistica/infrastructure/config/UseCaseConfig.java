package com.logistica.infrastructure.config;

import com.logistica.application.usecases.pago.ConsultarEstadoPagoUseCase;
import com.logistica.domain.repositories.PagoRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public ConsultarEstadoPagoUseCase consultarEstadoPagoUseCase(PagoRepository pagoRepository) {
        return new ConsultarEstadoPagoUseCase(pagoRepository);
    }
}
