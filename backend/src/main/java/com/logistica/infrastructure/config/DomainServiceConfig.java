package com.logistica.infrastructure.config;

import com.logistica.domain.repositories.PagoRepository;
import com.logistica.domain.services.AuditoriaPagoService;
import com.logistica.domain.services.IdempotenciaService;
import com.logistica.domain.services.ProcesadorEstadoPagoService;
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
