package com.logistica.VisualizarEstadoPago.infrastructure.config;

import com.logistica.VisualizarEstadoPago.application.usecases.pago.ConsultarEstadoPagoUseCase;
import com.logistica.VisualizarEstadoPago.application.usecases.pago.ListarPagosUseCase;
import com.logistica.VisualizarEstadoPago.domain.repositories.PagoRepository;
import com.logistica.VisualizarEstadoPago.domain.services.AuditoriaPagoService;
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
