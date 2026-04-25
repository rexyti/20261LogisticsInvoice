package com.logistica.infrastructure.adapters;

import com.logistica.domain.models.LogSincronizacion;
import com.logistica.domain.repositories.LogSincronizacionRepository;
import com.logistica.infrastructure.persistence.repositories.LogSincronizacionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class LogSincronizacionRepositoryAdapter implements LogSincronizacionRepository {

    private final LogSincronizacionJpaRepository jpaRepository;
    private final PaqueteMapper mapper;

    @Override
    public LogSincronizacion save(LogSincronizacion log) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(log)));
    }

    @Override
    public List<LogSincronizacion> findByIdPaquete(UUID idPaquete) {
        return jpaRepository.findByIdPaqueteOrderByTimestampDesc(idPaquete).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
