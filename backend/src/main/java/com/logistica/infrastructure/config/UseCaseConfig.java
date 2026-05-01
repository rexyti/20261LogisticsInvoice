package com.logistica.infrastructure.config;

import com.logistica.application.mappers.PagoDtoMapper;
import com.logistica.application.usecases.pago.ConsultarEstadoPagoUseCase;
import com.logistica.domain.repositories.PagoRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public PagoDtoMapper pagoDtoMapper() {
        return new PagoDtoMapper();
    }

    @Bean
    public ConsultarEstadoPagoUseCase consultarEstadoPagoUseCase(PagoRepository pagoRepository, PagoDtoMapper pagoDtoMapper) {
        return new ConsultarEstadoPagoUseCase(pagoRepository, pagoDtoMapper);
    }
}
