package com.logistica.infrastructure.persistence.repositories;

import com.logistica.domain.models.Contrato;
import com.logistica.domain.repositories.ContratoRepository;
import com.logistica.infrastructure.persistence.mapper.ContratoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ContratoRepositoryImpl implements ContratoRepository {

    private final ContratoJpaRepository jpaRepository;
    private final ContratoMapper mapper;

    @Override
    public Contrato save(Contrato contrato) {
        var entity = mapper.toEntity(contrato);
        var saved = jpaRepository.save(entity);
        return mapper.toModel(saved);
    }

    @Override
    public Optional<Contrato> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toModel);
    }
}
