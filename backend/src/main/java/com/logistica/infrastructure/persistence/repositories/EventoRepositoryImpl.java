package com.logistica.infrastructure.persistence.repositories;

import com.logistica.domain.models.EventoTransaccion;
import com.logistica.domain.repositories.EventoRepository;
import com.logistica.infrastructure.adapters.EventoMapper;
import com.logistica.infrastructure.persistence.entities.EventoEntity;
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
    public Optional<EventoTransaccion> findById(UUID id) {
        return jpaRepository.findById(id).map(eventoMapper::toDomain);
    }

    @Override
    public EventoTransaccion save(EventoTransaccion eventoTransaccion) {
        EventoEntity entity = eventoMapper.toEntity(eventoTransaccion);
        EventoEntity savedEntity = jpaRepository.save(entity);
        return eventoMapper.toDomain(savedEntity);
    }
}
