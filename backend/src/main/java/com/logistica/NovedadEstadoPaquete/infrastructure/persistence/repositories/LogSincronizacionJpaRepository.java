package com.logistica.NovedadEstadoPaquete.infrastructure.persistence.repositories;

import com.logistica.NovedadEstadoPaquete.infrastructure.persistence.entities.LogSincronizacionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogSincronizacionJpaRepository extends JpaRepository<LogSincronizacionEntity, Long> {

    Page<LogSincronizacionEntity> findByIdPaquete(Long idPaquete, Pageable pageable);

    List<LogSincronizacionEntity> findByIdPaqueteOrderByCreatedAtDesc(Long idPaquete);
}