package com.logistica.infrastructure.novedadEstadoPaquete.persistence.repositories;

import com.logistica.domain.novedadEstadoPaquete.models.NovedadEstadoPaquetePaquete;
import com.logistica.domain.novedadEstadoPaquete.repositories.PaqueteRepository;
import com.logistica.infrastructure.novedadEstadoPaquete.persistence.mapper.PaqueteEntityMapper;
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
