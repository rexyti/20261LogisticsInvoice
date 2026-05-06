package com.logistica.infrastructure.novedadEstadoPaquete.persistence.repositories;

import com.logistica.infrastructure.novedadEstadoPaquete.persistence.entities.LogSincronizacionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogSincronizacionJpaRepository extends JpaRepository<LogSincronizacionEntity, Long> {

    Page<LogSincronizacionEntity> findByIdPaquete(Long idPaquete, Pageable pageable);
}