package com.logistica.infrastructure.visualizarEstadoPago.config;

import com.logistica.application.visualizarEstadoPago.usecases.pago.ConsultarEstadoPagoUseCase;
import com.logistica.application.visualizarEstadoPago.usecases.pago.ListarPagosUseCase;
import com.logistica.domain.visualizarEstadoPago.repositories.VisualizarEstadoPagoPagoRepository;
import com.logistica.domain.visualizarEstadoPago.services.AuditoriaPagoService;
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
