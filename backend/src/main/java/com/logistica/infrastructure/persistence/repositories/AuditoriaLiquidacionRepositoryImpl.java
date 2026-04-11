package com.logistica.infrastructure.persistence.repositories;

import com.logistica.domain.models.AuditoriaLiquidacion;
import com.logistica.domain.repositories.AuditoriaLiquidacionRepository;
import com.logistica.infrastructure.adapters.AuditoriaLiquidacionMapper;
import com.logistica.infrastructure.persistence.entities.AuditoriaLiquidacionEntity;
import org.springframework.stereotype.Repository;

@Repository
public class AuditoriaLiquidacionRepositoryImpl implements AuditoriaLiquidacionRepository {

    private final AuditoriaLiquidacionJpaRepository jpaRepository;
    private final AuditoriaLiquidacionMapper mapper;

    public AuditoriaLiquidacionRepositoryImpl(AuditoriaLiquidacionJpaRepository jpaRepository, AuditoriaLiquidacionMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public AuditoriaLiquidacion save(AuditoriaLiquidacion auditoriaLiquidacion) {
        AuditoriaLiquidacionEntity entity = mapper.toEntity(auditoriaLiquidacion);
        AuditoriaLiquidacionEntity savedEntity = jpaRepository.save(entity);
        return mapper.toModel(savedEntity);
    }
}
