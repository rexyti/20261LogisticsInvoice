package com.logistica.infrastructure.persistence.repositories;

import com.logistica.domain.models.Liquidacion;
import com.logistica.domain.repositories.LiquidacionRepository;
import com.logistica.infrastructure.adapters.LiquidacionMapper;
import com.logistica.infrastructure.persistence.entities.LiquidacionEntity;
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
    public boolean existsByIdRuta(UUID idRuta) {
        return jpaRepository.existsByIdRuta(idRuta);
    }
}
