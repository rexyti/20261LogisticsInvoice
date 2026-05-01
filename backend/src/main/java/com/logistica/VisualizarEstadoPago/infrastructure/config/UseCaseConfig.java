package com.logistica.VisualizarEstadoPago.infrastructure.config;

import com.logistica.VisualizarEstadoPago.application.usecases.pago.ConsultarEstadoPagoUseCase;
import com.logistica.VisualizarEstadoPago.application.usecases.pago.ListarPagosUseCase;
import com.logistica.VisualizarEstadoPago.domain.repositories.VisualizarEstadoPagoPagoRepository;
import com.logistica.VisualizarEstadoPago.domain.services.AuditoriaPagoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public ConsultarEstadoPagoUseCase consultarEstadoPagoUseCase(
            VisualizarEstadoPagoPagoRepository pagoRepository,
            AuditoriaPagoService auditoriaPagoService) {
        return new ConsultarEstadoPagoUseCase(pagoRepository, auditoriaPagoService);
    }

    @Bean
    public ListarPagosUseCase listarPagosUseCase(VisualizarEstadoPagoPagoRepository pagoRepository) {
        return new ListarPagosUseCase(pagoRepository);
    }
}
