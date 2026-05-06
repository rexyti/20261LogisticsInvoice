package com.logistica.infrastructure.novedadEstadoPaquete.persistence.repositories;

import com.logistica.infrastructure.novedadEstadoPaquete.persistence.entities.HistorialEstadoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistorialJpaRepository extends JpaRepository<HistorialEstadoEntity, Long> {

    Page<HistorialEstadoEntity> findByIdPaquete(Long idPaquete, Pageable pageable);
}