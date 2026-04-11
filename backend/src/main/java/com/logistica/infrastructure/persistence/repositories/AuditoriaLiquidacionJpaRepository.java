package com.logistica.infrastructure.persistence.repositories;

import com.logistica.infrastructure.persistence.entities.AuditoriaLiquidacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuditoriaLiquidacionJpaRepository extends JpaRepository<AuditoriaLiquidacionEntity, UUID> {
}
