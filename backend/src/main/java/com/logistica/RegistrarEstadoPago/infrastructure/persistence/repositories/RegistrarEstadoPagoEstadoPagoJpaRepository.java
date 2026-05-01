package com.logistica.RegistrarEstadoPago.infrastructure.persistence.repositories;

import com.logistica.RegistrarEstadoPago.infrastructure.persistence.entities.RegistrarEstadoPagoEstadoPagoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RegistrarEstadoPagoEstadoPagoJpaRepository extends JpaRepository<RegistrarEstadoPagoEstadoPagoEntity, UUID> {
    List<RegistrarEstadoPagoEstadoPagoEntity> findByIdPagoOrderByFechaRegistroDesc(UUID idPago);
    Optional<RegistrarEstadoPagoEstadoPagoEntity> findFirstByIdPagoOrderByFechaRegistroDescSecuenciaEventoDesc(UUID idPago);
}
