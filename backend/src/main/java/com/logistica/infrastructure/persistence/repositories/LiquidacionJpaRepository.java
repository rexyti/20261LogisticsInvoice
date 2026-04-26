package com.logistica.infrastructure.persistence.repositories;

import com.logistica.infrastructure.persistence.entities.LiquidacionReferenciaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LiquidacionJpaRepository extends JpaRepository<LiquidacionReferenciaEntity, UUID> {
}
