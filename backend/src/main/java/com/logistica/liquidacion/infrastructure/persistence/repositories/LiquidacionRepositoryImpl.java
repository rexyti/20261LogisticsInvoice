package com.logistica.liquidacion.infrastructure.persistence.repositories;

import com.logistica.liquidacion.domain.models.Liquidacion;
import com.logistica.liquidacion.domain.repositories.LiquidacionRepository;
import com.logistica.liquidacion.infrastructure.persistence.mapper.LiquidacionMapper;
import com.logistica.liquidacion.infrastructure.persistence.entities.LiquidacionEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class LiquidacionRepositoryImpl implements LiquidacionRepository {

    private final LiquidacionJpaRepository jpaRepository;
    private final LiquidacionMapper mapper;

    public LiquidacionRepositoryImpl(LiquidacionJpaRepository jpaRepository, LiquidacionMapper mapper) {
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
