package com.logistica.infrastructure.config;

import com.logistica.domain.repositories.EventoTransaccionRepository;
import com.logistica.domain.services.EstadoPagoDomainService;
import com.logistica.domain.services.IdempotenciaEventoPagoService;
import com.logistica.domain.services.TransicionEstadoPagoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainServiceConfig {

    @Bean
    public EstadoPagoDomainService estadoPagoDomainService() {
        return new EstadoPagoDomainService();
    }

    @Bean
    public TransicionEstadoPagoService transicionEstadoPagoService() {
        return new TransicionEstadoPagoService();
    }

    @Bean
    public IdempotenciaEventoPagoService idempotenciaEventoPagoService(
            EventoTransaccionRepository eventoTransaccionRepository) {
        return new IdempotenciaEventoPagoService(eventoTransaccionRepository);
    }
}
