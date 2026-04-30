package com.logistica.NovedadEstadoPaquete.infrastructure.persistence.repositories;

import com.logistica.NovedadEstadoPaquete.domain.models.HistorialEstado;
import com.logistica.NovedadEstadoPaquete.domain.repositories.HistorialRepository;
import com.logistica.NovedadEstadoPaquete.infrastructure.persistence.mapper.PaqueteEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HistorialRepositoryImpl implements HistorialRepository {

    private static final int DEFAULT_SIZE = 100;

    private final HistorialJpaRepository jpa;
    private final PaqueteEntityMapper mapper;

    @Override
    public HistorialEstado save(HistorialEstado historialEstado) {
        return mapper.toDomain(
                jpa.save(
                        mapper.toEntity(historialEstado)
                )
        );
    }

    @Override
    public List<HistorialEstado> findByIdPaquete(Long idPaquete, int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = sanitizeSize(size);

        PageRequest pageRequest = PageRequest.of(
                safePage,
                safeSize,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return jpa.findByIdPaquete(idPaquete, pageRequest)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    private int sanitizeSize(int size) {
        if (size <= 0) {
            return DEFAULT_SIZE;
        }

        return Math.min(size, DEFAULT_SIZE);
    }
}