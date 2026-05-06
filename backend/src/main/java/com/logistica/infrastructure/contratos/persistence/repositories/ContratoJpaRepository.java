package com.logistica.infrastructure.contratos.persistence.repositories;

import com.logistica.infrastructure.contratos.persistence.entities.ContratoEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ContratoJpaRepository extends JpaRepository<ContratoEntity, UUID> {

    @EntityGraph(attributePaths = {"transportista", "seguro"})
    Optional<ContratoEntity> findByIdContrato(String idContrato);

    boolean existsByIdContrato(String idContrato);
}
