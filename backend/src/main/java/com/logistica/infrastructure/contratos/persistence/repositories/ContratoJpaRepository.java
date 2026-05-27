package com.logistica.infrastructure.contratos.persistence.repositories;

import com.logistica.infrastructure.contratos.persistence.entities.ContratoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ContratoJpaRepository extends JpaRepository<ContratoEntity, UUID> {


    Optional<ContratoEntity> findFirstByTransportista_IdOrderByFechaInicioDesc(UUID transportistaId);

    @EntityGraph(attributePaths = {"transportista", "seguro"})
    Optional<ContratoEntity> findByIdContrato(String idContrato);

    boolean existsByIdContrato(String idContrato);

    @EntityGraph(attributePaths = {"transportista", "seguro"})
    Page<ContratoEntity> findAllByTransportista_Id(UUID idTransportista, Pageable pageable);
}
