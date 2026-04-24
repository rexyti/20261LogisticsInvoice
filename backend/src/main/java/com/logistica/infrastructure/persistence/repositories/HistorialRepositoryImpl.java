package com.logistica.infrastructure.persistence.repositories;

import com.logistica.domain.models.HistorialEstado;
import com.logistica.domain.repositories.HistorialRepository;
import com.logistica.infrastructure.persistence.mapper.PaqueteEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HistorialRepositoryImpl implements HistorialRepository {

    private final HistorialJpaRepository jpa;
    private final PaqueteEntityMapper mapper;

    @Override
    public HistorialEstado save(HistorialEstado historial) {
        return mapper.toDomain(jpa.save(mapper.toEntity(historial)));
    }

    @Override
    public List<HistorialEstado> findByIdPaqueteOrderByFechaDesc(Long idPaquete) {
        return jpa.findByIdPaqueteOrderByFechaDesc(idPaquete)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
