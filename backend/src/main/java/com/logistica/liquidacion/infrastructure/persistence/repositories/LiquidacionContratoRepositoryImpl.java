package com.logistica.liquidacion.infrastructure.persistence.repositories;

import com.logistica.liquidacion.domain.models.LiquidacionContrato;
import com.logistica.liquidacion.domain.repositories.LiquidacionContratoRepository;
import com.logistica.liquidacion.infrastructure.persistence.mapper.LiquidacionContratoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class LiquidacionContratoRepositoryImpl implements LiquidacionContratoRepository {

    private final LiquidacionContratoJpaRepository jpaRepository;
    private final LiquidacionContratoMapper mapper;

    @Override
    public LiquidacionContrato save(LiquidacionContrato contrato) {
        var entity = mapper.toEntity(contrato);
        var saved = jpaRepository.save(entity);
        return mapper.toModel(saved);
    }

    @Override
    public Optional<LiquidacionContrato> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toModel);
    }
}
