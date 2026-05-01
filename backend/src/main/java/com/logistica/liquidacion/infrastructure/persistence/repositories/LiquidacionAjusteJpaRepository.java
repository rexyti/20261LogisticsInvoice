package com.logistica.liquidacion.infrastructure.persistence.repositories;

import com.logistica.liquidacion.infrastructure.persistence.entities.LiquidacionAjusteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LiquidacionAjusteJpaRepository extends JpaRepository<LiquidacionAjusteEntity, UUID> {
    List<LiquidacionAjusteEntity> findByLiquidacion_Id(UUID liquidacionId);
}
