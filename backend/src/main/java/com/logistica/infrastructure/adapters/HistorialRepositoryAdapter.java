package com.logistica.infrastructure.adapters;

import com.logistica.domain.models.HistorialEstado;
import com.logistica.domain.repositories.HistorialRepository;
import com.logistica.infrastructure.persistence.repositories.HistorialEstadoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class HistorialRepositoryAdapter implements HistorialRepository {

    private final HistorialEstadoJpaRepository jpaRepository;
    private final PaqueteMapper mapper;

    @Override
    public HistorialEstado save(HistorialEstado historial) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(historial)));
    }

    @Override
    public Page<HistorialEstado> findByIdPaquete(UUID idPaquete, Pageable pageable) {
        return jpaRepository.findByIdPaquete(idPaquete, pageable).map(mapper::toDomain);
    }
}
