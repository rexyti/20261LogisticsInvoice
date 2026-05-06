package com.logistica.infrastructure.visualizarEstadoPago.persistence.repositories;

import com.logistica.domain.visualizarEstadoPago.models.VisualizarEstadoPagoEventoTransaccion;
import com.logistica.domain.visualizarEstadoPago.repositories.EventoRepository;
import com.logistica.infrastructure.visualizarEstadoPago.adapters.EventoMapper;
import com.logistica.infrastructure.visualizarEstadoPago.persistence.entities.EventoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class EventoRepositoryImpl implements EventoRepository {

    @Autowired
    private EventoJpaRepository jpaRepository;

    @Autowired
    private EventoMapper eventoMapper;

    @Override
    public Optional<VisualizarEstadoPagoEventoTransaccion> findById(UUID id) {
        return jpaRepository.findById(id).map(eventoMapper::toDomain);
    }

    @Override
    public VisualizarEstadoPagoEventoTransaccion save(VisualizarEstadoPagoEventoTransaccion eventoTransaccion) {
        EventoEntity entity = eventoMapper.toEntity(eventoTransaccion);
        EventoEntity savedEntity = jpaRepository.save(entity);
        return eventoMapper.toDomain(savedEntity);
    }
}
