package com.logistica.infrastructure.config;

import com.logistica.application.usecases.pago.ConsultarEstadoPagoUseCase;
import com.logistica.application.usecases.pago.ListarPagosUseCase;
import com.logistica.domain.repositories.PagoRepository;
import com.logistica.domain.services.AuditoriaPagoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public ConsultarEstadoPagoUseCase consultarEstadoPagoUseCase(
            PagoRepository pagoRepository,
            AuditoriaPagoService auditoriaPagoService) {
        return new ConsultarEstadoPagoUseCase(pagoRepository, auditoriaPagoService);
    }

    @Bean
    public ListarPagosUseCase listarPagosUseCase(PagoRepository pagoRepository) {
        return new ListarPagosUseCase(pagoRepository);
    }
}
