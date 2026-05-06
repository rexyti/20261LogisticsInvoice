package com.logistica.infrastructure.liquidacion.persistence.repositories;

import com.logistica.infrastructure.liquidacion.persistence.entities.ContratoTarifaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LiquidacionContratoJpaRepository extends JpaRepository<ContratoTarifaEntity, UUID> {
}
