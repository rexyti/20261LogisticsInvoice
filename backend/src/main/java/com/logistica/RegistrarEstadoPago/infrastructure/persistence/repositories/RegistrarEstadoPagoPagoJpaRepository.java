package com.logistica.RegistrarEstadoPago.infrastructure.persistence.repositories;

import com.logistica.RegistrarEstadoPago.infrastructure.persistence.entities.RegistrarEstadoPagoPagoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RegistrarEstadoPagoPagoJpaRepository extends JpaRepository<RegistrarEstadoPagoPagoEntity, UUID> {
    List<RegistrarEstadoPagoPagoEntity> findByIdLiquidacion(UUID idLiquidacion);
}
