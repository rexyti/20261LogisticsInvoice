package com.logistica.infrastructure.visualizarEstadoPago.config;

import com.logistica.domain.visualizarEstadoPago.repositories.VisualizarEstadoPagoPagoRepository;
import com.logistica.domain.visualizarEstadoPago.services.AuditoriaPagoService;
import com.logistica.domain.visualizarEstadoPago.services.IdempotenciaService;
import com.logistica.domain.visualizarEstadoPago.services.ProcesadorEstadoPagoService;
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
