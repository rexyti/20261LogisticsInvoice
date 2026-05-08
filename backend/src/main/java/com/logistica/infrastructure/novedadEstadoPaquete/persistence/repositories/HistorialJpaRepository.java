package com.logistica.infrastructure.novedadEstadoPaquete.persistence.repositories;

import com.logistica.infrastructure.novedadEstadoPaquete.persistence.entities.HistorialEstadoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HistorialJpaRepository extends JpaRepository<HistorialEstadoEntity, Long> {

    Page<HistorialEstadoEntity> findByIdPaquete(UUID idPaquete, Pageable pageable);
}