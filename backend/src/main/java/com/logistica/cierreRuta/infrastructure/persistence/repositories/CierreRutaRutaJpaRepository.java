package com.logistica.cierreRuta.infrastructure.persistence.repositories;

import com.logistica.cierreRuta.domain.enums.EstadoProcesamiento;
import com.logistica.cierreRuta.infrastructure.persistence.entities.CierreRutaRutaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CierreRutaRutaJpaRepository extends JpaRepository<CierreRutaRutaEntity, UUID> {
    boolean existsByRutaId(UUID rutaId);
    Optional<CierreRutaRutaEntity> findByRutaId(UUID rutaId);
    Page<CierreRutaRutaEntity> findByEstadoProcesamiento(EstadoProcesamiento estado, Pageable pageable);
}
