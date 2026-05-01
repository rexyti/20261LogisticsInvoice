package com.logistica.VisualizarEstadoPago.infrastructure.config;

import com.logistica.VisualizarEstadoPago.domain.repositories.PagoRepository;
import com.logistica.VisualizarEstadoPago.domain.services.AuditoriaPagoService;
import com.logistica.VisualizarEstadoPago.domain.services.IdempotenciaService;
import com.logistica.VisualizarEstadoPago.domain.services.ProcesadorEstadoPagoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainServiceConfig {

    @Bean
    public ProcesadorEstadoPagoService procesadorEstadoPagoService(PagoRepository pagoRepository) {
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
