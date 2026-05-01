package com.logistica.VisualizarEstadoPago.infrastructure.persistence.repositories;

import com.logistica.VisualizarEstadoPago.domain.models.VisualizarEstadoPagoEstadoPago;
import com.logistica.VisualizarEstadoPago.domain.repositories.VisualizarEstadoPagoEstadoPagoRepository;
import com.logistica.VisualizarEstadoPago.infrastructure.adapters.EstadoPagoMapper;
import com.logistica.VisualizarEstadoPago.infrastructure.persistence.entities.VisualizarEstadoPagoEstadoPagoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class EstadoPagoRepositoryImpl implements VisualizarEstadoPagoEstadoPagoRepository {

    @Autowired
    private VisualizarEstadoPagoEstadoPagoJpaRepository jpaRepository;

    @Autowired
    private EstadoPagoMapper estadoPagoMapper;

    @Override
    public Optional<VisualizarEstadoPagoEstadoPago> findById(UUID id) {
        return jpaRepository.findById(id).map(estadoPagoMapper::toDomain);
    }

    @Override
    public VisualizarEstadoPagoEstadoPago save(VisualizarEstadoPagoEstadoPago estadoPago) {
        VisualizarEstadoPagoEstadoPagoEntity entity = estadoPagoMapper.toEntity(estadoPago);
        VisualizarEstadoPagoEstadoPagoEntity savedEntity = jpaRepository.save(entity);
        return estadoPagoMapper.toDomain(savedEntity);
    }
}
