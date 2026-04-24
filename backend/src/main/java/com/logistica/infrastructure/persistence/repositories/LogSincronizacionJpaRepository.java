package com.logistica.infrastructure.persistence.repositories;

import com.logistica.infrastructure.persistence.entities.LogSincronizacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LogSincronizacionJpaRepository extends JpaRepository<LogSincronizacionEntity, UUID> {
    List<LogSincronizacionEntity> findByIdPaquete(UUID idPaquete);
}
