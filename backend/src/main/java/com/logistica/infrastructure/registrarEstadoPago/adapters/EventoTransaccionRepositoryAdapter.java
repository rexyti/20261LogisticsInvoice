package com.logistica.infrastructure.registrarEstadoPago.adapters;

import com.logistica.domain.registrarEstadoPago.models.RegistrarEstadoPagoEventoTransaccion;
import com.logistica.domain.registrarEstadoPago.repositories.EventoTransaccionRepository;
import com.logistica.infrastructure.registrarEstadoPago.persistence.entities.EventoTransaccionEntity;
import com.logistica.infrastructure.registrarEstadoPago.persistence.repositories.EventoTransaccionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EventoTransaccionRepositoryAdapter implements EventoTransaccionRepository {

    private final EventoTransaccionJpaRepository eventoTransaccionJpaRepository;
    private final PagoMapper pagoMapper;

    @Override
    public RegistrarEstadoPagoEventoTransaccion save(RegistrarEstadoPagoEventoTransaccion evento) {
        EventoTransaccionEntity entity = pagoMapper.toEntity(evento);
        EventoTransaccionEntity saved = eventoTransaccionJpaRepository.save(entity);
        return pagoMapper.toDomain(saved);
    }

    @Override
    public Optional<RegistrarEstadoPagoEventoTransaccion> findByIdTransaccionBanco(String idTransaccionBanco) {
        return eventoTransaccionJpaRepository.findByIdTransaccionBanco(idTransaccionBanco)
                .map(pagoMapper::toDomain);
    }

    @Override
    public List<RegistrarEstadoPagoEventoTransaccion> findByIdPago(UUID idPago) {
        return eventoTransaccionJpaRepository.findByIdPagoOrderByFechaRecepcionAsc(idPago)
                .stream().map(pagoMapper::toDomain).toList();
    }
}
