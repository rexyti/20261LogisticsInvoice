package com.logistica.infrastructure.cierreRuta.persistence.repositories;

import com.logistica.infrastructure.cierreRuta.persistence.entities.TransportistaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CierreRutaTransportistaJpaRepository extends JpaRepository<TransportistaEntity, UUID> {
    Optional<TransportistaEntity> findByConductorId(UUID transportistaId);
}
