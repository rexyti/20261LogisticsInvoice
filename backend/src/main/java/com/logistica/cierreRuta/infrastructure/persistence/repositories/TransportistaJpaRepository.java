package com.logistica.cierreRuta.infrastructure.persistence.repositories;


import com.logistica.cierreRuta.infrastructure.persistence.entities.TransportistaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TransportistaJpaRepository extends JpaRepository<TransportistaEntity, UUID> {

    Optional<TransportistaEntity> findByConductorId(UUID transportistaId);
}
