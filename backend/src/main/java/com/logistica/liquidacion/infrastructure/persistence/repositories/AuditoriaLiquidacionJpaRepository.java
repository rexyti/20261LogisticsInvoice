package com.logistica.liquidacion.infrastructure.persistence.repositories;

import com.logistica.liquidacion.infrastructure.persistence.entities.AuditoriaLiquidacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuditoriaLiquidacionJpaRepository extends JpaRepository<AuditoriaLiquidacionEntity, UUID> {
    List<AuditoriaLiquidacionEntity> findByIdLiquidacion(UUID idLiquidacion);
}
