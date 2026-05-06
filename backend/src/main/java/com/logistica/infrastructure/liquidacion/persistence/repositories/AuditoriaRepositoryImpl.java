package com.logistica.infrastructure.liquidacion.persistence.repositories;

import com.logistica.domain.liquidacion.models.AuditoriaLiquidacion;
import com.logistica.domain.liquidacion.repositories.AuditoriaLiquidacionRepository;
import com.logistica.infrastructure.liquidacion.persistence.mapper.AuditoriaMapper;
import com.logistica.infrastructure.liquidacion.persistence.entities.AuditoriaEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class AuditoriaRepositoryImpl implements AuditoriaLiquidacionRepository {

    private final AuditoriaJpaRepository jpaRepository;
    private final AuditoriaMapper mapper;

    public AuditoriaRepositoryImpl(AuditoriaJpaRepository jpaRepository, AuditoriaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public AuditoriaLiquidacion save(AuditoriaLiquidacion auditoriaLiquidacion) {
        AuditoriaEntity entity = mapper.toEntity(auditoriaLiquidacion);
        AuditoriaEntity savedEntity = jpaRepository.save(entity);
        return mapper.toModel(savedEntity);
    }

    @Override
    public List<AuditoriaLiquidacion> findByIdLiquidacion(UUID liquidacionId) {
        return jpaRepository.findByIdLiquidacion(liquidacionId).stream()
                .map(mapper::toModel)
                .collect(Collectors.toList());
    }
}
