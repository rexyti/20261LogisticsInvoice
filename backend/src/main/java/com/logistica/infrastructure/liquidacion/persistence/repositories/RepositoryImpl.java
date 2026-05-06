package com.logistica.infrastructure.liquidacion.persistence.repositories;

import com.logistica.domain.liquidacion.models.Liquidacion;
import com.logistica.domain.liquidacion.repositories.LiquidacionRepository;
import com.logistica.infrastructure.liquidacion.persistence.mapper.Mapper;
import com.logistica.infrastructure.liquidacion.persistence.entities.LiquidacionEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class RepositoryImpl implements LiquidacionRepository {

    private final LiquidacionJpaRepository jpaRepository;
    private final Mapper mapper;

    public RepositoryImpl(LiquidacionJpaRepository jpaRepository, Mapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Liquidacion save(Liquidacion liquidacion) {
        LiquidacionEntity entity = mapper.toEntity(liquidacion);
        LiquidacionEntity savedEntity = jpaRepository.save(entity);
        return mapper.toModel(savedEntity);
    }

    @Override
    public Optional<Liquidacion> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toModel);
    }

    @Override
    public Optional<Liquidacion> findByIdRuta(UUID idRuta) {
        return jpaRepository.findByIdRuta(idRuta).map(mapper::toModel);
    }

    @Override
    public boolean existsByIdRuta(UUID idRuta) {
        return jpaRepository.existsByIdRuta(idRuta);
    }
}
