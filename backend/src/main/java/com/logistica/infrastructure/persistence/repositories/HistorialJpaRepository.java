package com.logistica.infrastructure.persistence.repositories;

import com.logistica.infrastructure.persistence.entities.HistorialEstadoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistorialJpaRepository extends JpaRepository<HistorialEstadoEntity, Long> {

    List<HistorialEstadoEntity> findByIdPaqueteOrderByFechaDesc(Long idPaquete);
}
