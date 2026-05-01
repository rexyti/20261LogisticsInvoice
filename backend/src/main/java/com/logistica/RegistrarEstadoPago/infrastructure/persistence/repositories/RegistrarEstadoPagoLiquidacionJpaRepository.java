package com.logistica.RegistrarEstadoPago.infrastructure.persistence.repositories;

import com.logistica.RegistrarEstadoPago.infrastructure.persistence.entities.LiquidacionReferenciaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RegistrarEstadoPagoLiquidacionJpaRepository extends JpaRepository<LiquidacionReferenciaEntity, UUID> {
}
