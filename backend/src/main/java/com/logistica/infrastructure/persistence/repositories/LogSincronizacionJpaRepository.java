package com.logistica.infrastructure.persistence.repositories;

import com.logistica.infrastructure.persistence.entities.LogSincronizacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogSincronizacionJpaRepository extends JpaRepository<LogSincronizacionEntity, Long> {

    List<LogSincronizacionEntity> findByIdPaquete(Long idPaquete);
}
