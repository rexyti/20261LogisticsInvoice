package com.logistica.RegistrarEstadoPago.infrastructure.config;

import com.logistica.RegistrarEstadoPago.domain.repositories.EventoTransaccionRepository;
import com.logistica.RegistrarEstadoPago.domain.services.EstadoPagoDomainService;
import com.logistica.RegistrarEstadoPago.domain.services.IdempotenciaEventoPagoService;
import com.logistica.RegistrarEstadoPago.domain.services.TransicionEstadoPagoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RegistrarEstadoPagoDomainServiceConfig {

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
