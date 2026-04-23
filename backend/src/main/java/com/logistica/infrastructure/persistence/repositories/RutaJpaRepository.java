package com.logistica.infrastructure.persistence.repositories;

import com.logistica.infrastructure.persistence.entities.RutaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RutaJpaRepository extends JpaRepository<RutaEntity, UUID> {
    boolean existsByRutaId(UUID rutaId);
    Optional<RutaEntity> findByRutaId(UUID rutaId);
}
