package com.logistica.liquidacion.infrastructure.persistence.repositories;

import com.logistica.liquidacion.infrastructure.persistence.entities.LiquidacionContratoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LiquidacionContratoJpaRepository extends JpaRepository<LiquidacionContratoEntity, UUID> {
}
