package com.logistica.liquidacion.infrastructure.persistence.repositories;

import com.logistica.liquidacion.domain.models.AuditoriaLiquidacion;
import com.logistica.liquidacion.domain.repositories.AuditoriaLiquidacionRepository;
import com.logistica.liquidacion.infrastructure.persistence.mapper.AuditoriaLiquidacionMapper;
import com.logistica.liquidacion.infrastructure.persistence.entities.AuditoriaLiquidacionEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @Override
    public List<AuditoriaLiquidacion> findByIdLiquidacion(UUID liquidacionId) {
        return jpaRepository.findByIdLiquidacion(liquidacionId).stream()
                .map(mapper::toModel)
                .collect(Collectors.toList());
    }
}
