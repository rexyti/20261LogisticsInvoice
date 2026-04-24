package com.logistica.infrastructure.persistence.repositories;

import com.logistica.infrastructure.persistence.entities.HistorialEstadoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HistorialEstadoJpaRepository extends JpaRepository<HistorialEstadoEntity, UUID> {
    Page<HistorialEstadoEntity> findByIdPaquete(UUID idPaquete, Pageable pageable);
}
