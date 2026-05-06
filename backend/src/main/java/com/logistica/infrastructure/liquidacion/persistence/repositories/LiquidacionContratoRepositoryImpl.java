package com.logistica.infrastructure.liquidacion.persistence.repositories;

import com.logistica.domain.liquidacion.models.ContratoTarifa;
import com.logistica.domain.liquidacion.repositories.ContratoTarifaRepository;
import com.logistica.infrastructure.liquidacion.persistence.mapper.LiquidacionContratoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class LiquidacionContratoRepositoryImpl implements ContratoTarifaRepository {

    private final LiquidacionContratoJpaRepository jpaRepository;
    private final LiquidacionContratoMapper mapper;

    @Override
    public ContratoTarifa save(ContratoTarifa contrato) {
        var entity = mapper.toEntity(contrato);
        var saved = jpaRepository.save(entity);
        return mapper.toModel(saved);
    }

    @Override
    public Optional<ContratoTarifa> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toModel);
    }
}
