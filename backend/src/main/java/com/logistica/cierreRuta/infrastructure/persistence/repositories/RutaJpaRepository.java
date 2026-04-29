package com.logistica.cierreRuta.infrastructure.persistence.repositories;

import com.logistica.cierreRuta.domain.enums.EstadoProcesamiento;
import com.logistica.cierreRuta.infrastructure.persistence.entities.RutaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RutaJpaRepository extends JpaRepository<RutaEntity, UUID> {
    boolean existsByRutaId(UUID rutaId);
    Optional<RutaEntity> findByRutaId(UUID rutaId);
    Page<RutaEntity> findByEstadoProcesamiento(EstadoProcesamiento estado, Pageable pageable);
}
