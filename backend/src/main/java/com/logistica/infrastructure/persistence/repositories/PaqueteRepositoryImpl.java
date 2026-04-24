package com.logistica.infrastructure.persistence.repositories;

import com.logistica.domain.models.Paquete;
import com.logistica.domain.repositories.PaqueteRepository;
import com.logistica.infrastructure.persistence.mapper.PaqueteEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PaqueteRepositoryImpl implements PaqueteRepository {

    private final PaqueteJpaRepository jpa;
    private final PaqueteEntityMapper mapper;

    @Override
    public Optional<Paquete> findByIdPaquete(Long idPaquete) {
        return jpa.findByIdPaquete(idPaquete).map(mapper::toDomain);
    }

    @Override
    public Paquete save(Paquete paquete) {
        return mapper.toDomain(jpa.save(mapper.toEntity(paquete)));
    }
}
