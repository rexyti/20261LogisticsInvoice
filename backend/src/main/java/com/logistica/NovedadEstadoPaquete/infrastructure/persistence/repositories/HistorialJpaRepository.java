package com.logistica.NovedadEstadoPaquete.infrastructure.persistence.repositories;

import com.logistica.NovedadEstadoPaquete.infrastructure.persistence.entities.HistorialEstadoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistorialJpaRepository extends JpaRepository<HistorialEstadoEntity, Long> {

    Page<HistorialEstadoEntity> findByIdPaquete(Long idPaquete, Pageable pageable);
}