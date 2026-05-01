package com.logistica.liquidacion.infrastructure.persistence.repositories;

import com.logistica.liquidacion.infrastructure.persistence.entities.LiquidacionAuditoriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LiquidacionAuditoriaJpaRepository extends JpaRepository<LiquidacionAuditoriaEntity, UUID> {
    List<LiquidacionAuditoriaEntity> findByIdLiquidacion(UUID idLiquidacion);
}
