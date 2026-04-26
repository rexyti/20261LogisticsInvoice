package com.logistica.infrastructure.adapters;

import com.logistica.domain.models.EventoTransaccion;
import com.logistica.domain.repositories.EventoTransaccionRepository;
import com.logistica.infrastructure.persistence.entities.EventoTransaccionEntity;
import com.logistica.infrastructure.persistence.repositories.EventoTransaccionJpaRepository;
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
    public EventoTransaccion save(EventoTransaccion evento) {
        EventoTransaccionEntity entity = pagoMapper.toEntity(evento);
        EventoTransaccionEntity saved = eventoTransaccionJpaRepository.save(entity);
        return pagoMapper.toDomain(saved);
    }

    @Override
    public Optional<EventoTransaccion> findByIdTransaccionBanco(String idTransaccionBanco) {
        return eventoTransaccionJpaRepository.findByIdTransaccionBanco(idTransaccionBanco)
                .map(pagoMapper::toDomain);
    }

    @Override
    public List<EventoTransaccion> findByIdPago(UUID idPago) {
        return eventoTransaccionJpaRepository.findByIdPagoOrderByFechaRecepcionAsc(idPago)
                .stream().map(pagoMapper::toDomain).toList();
    }
}
