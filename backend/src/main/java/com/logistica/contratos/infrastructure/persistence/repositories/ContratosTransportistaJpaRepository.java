package com.logistica.contratos.infrastructure.persistence.repositories;

import com.logistica.contratos.infrastructure.persistence.entities.ContratosTransportistaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ContratosTransportistaJpaRepository extends JpaRepository<ContratosTransportistaEntity, UUID> {
}
