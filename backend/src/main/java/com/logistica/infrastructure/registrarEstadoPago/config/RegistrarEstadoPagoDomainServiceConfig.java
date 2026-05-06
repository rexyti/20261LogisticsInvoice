package com.logistica.infrastructure.registrarEstadoPago.config;

import com.logistica.domain.registrarEstadoPago.repositories.EventoTransaccionRepository;
import com.logistica.domain.registrarEstadoPago.services.EstadoPagoDomainService;
import com.logistica.domain.registrarEstadoPago.services.IdempotenciaEventoPagoService;
import com.logistica.domain.registrarEstadoPago.services.TransicionEstadoPagoService;
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
