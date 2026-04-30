package com.logistica.NovedadEstadoPaquete.infrastructure.persistence.repositories;

import com.logistica.NovedadEstadoPaquete.domain.models.LogSincronizacion;
import com.logistica.NovedadEstadoPaquete.domain.repositories.LogSincronizacionRepository;
import com.logistica.NovedadEstadoPaquete.infrastructure.persistence.mapper.PaqueteEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class LogSincronizacionRepositoryImpl implements LogSincronizacionRepository {

    private static final int DEFAULT_SIZE = 100;

    private final LogSincronizacionJpaRepository jpa;
    private final PaqueteEntityMapper mapper;

    @Override
    public LogSincronizacion save(LogSincronizacion log) {
        return mapper.toDomain(
                jpa.save(
                        mapper.toEntity(log)
                )
        );
    }

    @Override
    public List<LogSincronizacion> findAll(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = sanitizeSize(size);

        PageRequest pageRequest = PageRequest.of(
                safePage,
                safeSize,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return jpa.findAll(pageRequest)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<LogSincronizacion> findByIdPaquete(Long idPaquete, int page, int size) {
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