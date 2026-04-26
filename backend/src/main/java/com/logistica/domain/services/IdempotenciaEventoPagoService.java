package com.logistica.domain.services;

import com.logistica.domain.repositories.EventoTransaccionRepository;

public class IdempotenciaEventoPagoService {

    private final EventoTransaccionRepository eventoTransaccionRepository;

    public IdempotenciaEventoPagoService(EventoTransaccionRepository eventoTransaccionRepository) {
        this.eventoTransaccionRepository = eventoTransaccionRepository;
    }

    public boolean esEventoDuplicado(String idTransaccionBanco) {
        return eventoTransaccionRepository.findByIdTransaccionBanco(idTransaccionBanco)
                .filter(e -> e.procesado())
                .isPresent();
    }
}
