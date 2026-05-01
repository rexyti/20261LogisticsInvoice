package com.logistica.NovedadEstadoPaquete.infrastructure.persistence.repositories;

import com.logistica.NovedadEstadoPaquete.domain.models.NovedadEstadoPaquetePaquete;
import com.logistica.NovedadEstadoPaquete.domain.repositories.PaqueteRepository;
import com.logistica.NovedadEstadoPaquete.infrastructure.persistence.mapper.PaqueteEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PaqueteRepositoryImpl implements PaqueteRepository {

    private final PaqueteJpaRepository jpa;
    private final PaqueteEntityMapper mapper;

    @Override
    public Optional<NovedadEstadoPaquetePaquete> findByIdPaquete(Long idPaquete) {
        return jpa.findByIdPaquete(idPaquete).map(mapper::toDomain);
    }

    @Override
    public NovedadEstadoPaquetePaquete save(NovedadEstadoPaquetePaquete paquete) {
        return mapper.toDomain(jpa.save(mapper.toEntity(paquete)));
    }
}
