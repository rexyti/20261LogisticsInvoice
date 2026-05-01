package com.logistica.liquidacion.infrastructure.persistence.repositories;

import com.logistica.liquidacion.domain.models.AuditoriaLiquidacion;
import com.logistica.liquidacion.domain.repositories.AuditoriaLiquidacionRepository;
import com.logistica.liquidacion.infrastructure.persistence.mapper.LiquidacionAuditoriaMapper;
import com.logistica.liquidacion.infrastructure.persistence.entities.LiquidacionAuditoriaEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class LiquidacionAuditoriaRepositoryImpl implements AuditoriaLiquidacionRepository {

    private final LiquidacionAuditoriaJpaRepository jpaRepository;
    private final LiquidacionAuditoriaMapper mapper;

    public LiquidacionAuditoriaRepositoryImpl(LiquidacionAuditoriaJpaRepository jpaRepository, LiquidacionAuditoriaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public AuditoriaLiquidacion save(AuditoriaLiquidacion auditoriaLiquidacion) {
        LiquidacionAuditoriaEntity entity = mapper.toEntity(auditoriaLiquidacion);
        LiquidacionAuditoriaEntity savedEntity = jpaRepository.save(entity);
        return mapper.toModel(savedEntity);
    }

    @Override
    public List<AuditoriaLiquidacion> findByIdLiquidacion(UUID liquidacionId) {
        return jpaRepository.findByIdLiquidacion(liquidacionId).stream()
                .map(mapper::toModel)
                .collect(Collectors.toList());
    }
}
