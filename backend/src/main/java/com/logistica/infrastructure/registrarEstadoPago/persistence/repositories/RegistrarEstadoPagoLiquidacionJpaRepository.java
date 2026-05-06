package com.logistica.infrastructure.registrarEstadoPago.persistence.repositories;

import com.logistica.infrastructure.registrarEstadoPago.persistence.entities.LiquidacionReferenciaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RegistrarEstadoPagoLiquidacionJpaRepository extends JpaRepository<LiquidacionReferenciaEntity, UUID> {
}
