package com.logistica.RegistrarEstadoPago.infrastructure.persistence.repositories;

import com.logistica.RegistrarEstadoPago.infrastructure.persistence.entities.EstadoPagoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EstadoPagoJpaRepository extends JpaRepository<EstadoPagoEntity, UUID> {
    List<EstadoPagoEntity> findByIdPagoOrderByFechaRegistroDesc(UUID idPago);
    Optional<EstadoPagoEntity> findFirstByIdPagoOrderByFechaRegistroDescSecuenciaEventoDesc(UUID idPago);
}
