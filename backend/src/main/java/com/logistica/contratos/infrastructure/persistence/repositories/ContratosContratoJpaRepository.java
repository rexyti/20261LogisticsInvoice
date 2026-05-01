package com.logistica.contratos.infrastructure.persistence.repositories;

import com.logistica.contratos.infrastructure.persistence.entities.ContratosContratoEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ContratosContratoJpaRepository extends JpaRepository<ContratosContratoEntity, UUID> {

    @EntityGraph(attributePaths = {"transportista", "seguro"})
    Optional<ContratosContratoEntity> findByIdContrato(String idContrato);

    boolean existsByIdContrato(String idContrato);
}
