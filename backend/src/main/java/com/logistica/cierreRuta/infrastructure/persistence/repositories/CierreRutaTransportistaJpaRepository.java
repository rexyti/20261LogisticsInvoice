package com.logistica.cierreRuta.infrastructure.persistence.repositories;


import com.logistica.cierreRuta.infrastructure.persistence.entities.CierreRutaTransportistaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CierreRutaTransportistaJpaRepository extends JpaRepository<CierreRutaTransportistaEntity, UUID> {

    Optional<CierreRutaTransportistaEntity> findByConductorId(UUID transportistaId);
}
