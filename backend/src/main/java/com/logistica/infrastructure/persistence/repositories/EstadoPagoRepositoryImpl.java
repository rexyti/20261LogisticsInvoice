package com.logistica.infrastructure.persistence.repositories;

import com.logistica.domain.models.EstadoPago;
import com.logistica.domain.repositories.EstadoPagoRepository;
import com.logistica.infrastructure.adapters.EstadoPagoMapper;
import com.logistica.infrastructure.persistence.entities.EstadoPagoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class EstadoPagoRepositoryImpl implements EstadoPagoRepository {

    @Autowired
    private EstadoPagoJpaRepository jpaRepository;

    @Autowired
    private EstadoPagoMapper estadoPagoMapper;

    @Override
    public Optional<EstadoPago> findById(UUID id) {
        return jpaRepository.findById(id).map(estadoPagoMapper::toDomain);
    }

    @Override
    public EstadoPago save(EstadoPago estadoPago) {
        EstadoPagoEntity entity = estadoPagoMapper.toEntity(estadoPago);
        EstadoPagoEntity savedEntity = jpaRepository.save(entity);
        return estadoPagoMapper.toDomain(savedEntity);
    }
}
