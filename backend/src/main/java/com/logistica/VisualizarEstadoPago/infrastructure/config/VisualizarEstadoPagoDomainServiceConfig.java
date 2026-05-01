package com.logistica.VisualizarEstadoPago.infrastructure.config;

import com.logistica.VisualizarEstadoPago.domain.repositories.VisualizarEstadoPagoPagoRepository;
import com.logistica.VisualizarEstadoPago.domain.services.AuditoriaPagoService;
import com.logistica.VisualizarEstadoPago.domain.services.IdempotenciaService;
import com.logistica.VisualizarEstadoPago.domain.services.ProcesadorEstadoPagoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VisualizarEstadoPagoDomainServiceConfig {

    @Bean
    public ProcesadorEstadoPagoService procesadorEstadoPagoService(VisualizarEstadoPagoPagoRepository pagoRepository) {
        return new ProcesadorEstadoPagoService(pagoRepository);
    }

    @Bean
    public IdempotenciaService idempotenciaService() {
        return new IdempotenciaService();
    }

    @Bean
    public AuditoriaPagoService auditoriaPagoService() {
        return new AuditoriaPagoService();
    }
}
