package com.logistica.infrastructure.persistence.repositories;

import com.logistica.domain.models.LogSincronizacion;
import com.logistica.domain.repositories.LogSincronizacionRepository;
import com.logistica.infrastructure.persistence.mapper.PaqueteEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class LogSincronizacionRepositoryImpl implements LogSincronizacionRepository {

    private final LogSincronizacionJpaRepository jpa;
    private final PaqueteEntityMapper mapper;

    @Override
    public LogSincronizacion save(LogSincronizacion log) {
        return mapper.toDomain(jpa.save(mapper.toEntity(log)));
    }

    @Override
    public List<LogSincronizacion> findAll() {
        return jpa.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<LogSincronizacion> findByIdPaquete(Long idPaquete) {
        return jpa.findByIdPaquete(idPaquete).stream().map(mapper::toDomain).toList();
    }
}
