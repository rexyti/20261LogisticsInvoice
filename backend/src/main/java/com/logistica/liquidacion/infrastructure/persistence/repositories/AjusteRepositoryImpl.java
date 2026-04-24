package com.logistica.liquidacion.infrastructure.persistence.repositories;

import com.logistica.liquidacion.domain.models.Ajuste;
import com.logistica.liquidacion.domain.repositories.AjusteRepository;
import com.logistica.liquidacion.infrastructure.persistence.mapper.AjusteMapper;
import com.logistica.liquidacion.infrastructure.persistence.entities.AjusteEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class AjusteRepositoryImpl implements AjusteRepository {

    private final AjusteJpaRepository jpaRepository;
    private final AjusteMapper mapper;

    public AjusteRepositoryImpl(AjusteJpaRepository jpaRepository, AjusteMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Ajuste save(Ajuste ajuste) {
        AjusteEntity entity = mapper.toEntity(ajuste);
        AjusteEntity savedEntity = jpaRepository.save(entity);
        return mapper.toModel(savedEntity);
    }

    @Override
    public List<Ajuste> saveAll(List<Ajuste> ajustes) {
        List<AjusteEntity> entities = ajustes.stream()
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
