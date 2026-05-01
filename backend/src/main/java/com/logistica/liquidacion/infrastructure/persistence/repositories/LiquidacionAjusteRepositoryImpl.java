package com.logistica.liquidacion.infrastructure.persistence.repositories;

import com.logistica.liquidacion.domain.models.Ajuste;
import com.logistica.liquidacion.domain.repositories.AjusteRepository;
import com.logistica.liquidacion.infrastructure.persistence.mapper.LiquidacionAjusteMapper;
import com.logistica.liquidacion.infrastructure.persistence.entities.LiquidacionAjusteEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class LiquidacionAjusteRepositoryImpl implements AjusteRepository {

    private final LiquidacionAjusteJpaRepository jpaRepository;
    private final LiquidacionAjusteMapper mapper;

    public LiquidacionAjusteRepositoryImpl(LiquidacionAjusteJpaRepository jpaRepository, LiquidacionAjusteMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Ajuste save(Ajuste ajuste) {
        LiquidacionAjusteEntity entity = mapper.toEntity(ajuste);
        LiquidacionAjusteEntity savedEntity = jpaRepository.save(entity);
        return mapper.toModel(savedEntity);
    }

    @Override
    public List<Ajuste> saveAll(List<Ajuste> ajustes) {
        List<LiquidacionAjusteEntity> entities = ajustes.stream()
                .map(mapper::toEntity)
                .collect(Collectors.toList());
        return jpaRepository.saveAll(entities).stream()
                .map(mapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Ajuste> findByIdLiquidacion(UUID liquidacionId) {
        return jpaRepository.findByLiquidacion_Id(liquidacionId).stream()
                .map(mapper::toModel)
                .collect(Collectors.toList());
    }
}
