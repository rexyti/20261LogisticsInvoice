package com.logistica.infrastructure.novedadEstadoPaquete.persistence.repositories;

import com.logistica.infrastructure.novedadEstadoPaquete.persistence.entities.PaqueteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaqueteJpaRepository extends JpaRepository<PaqueteEntity, Long> {

    Optional<PaqueteEntity> findByIdPaquete(UUID idPaquete);
}
