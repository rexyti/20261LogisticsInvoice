package com.logistica.NovedadEstadoPaquete.infrastructure.persistence.repositories;

import com.logistica.NovedadEstadoPaquete.infrastructure.persistence.entities.HistorialEstadoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistorialJpaRepository extends JpaRepository<HistorialEstadoEntity, Long> {

    Page<HistorialEstadoEntity> findByIdPaquete(Long idPaquete, Pageable pageable);

    List<HistorialEstadoEntity> findByIdPaqueteOrderByFechaDesc(Long idPaquete);
}