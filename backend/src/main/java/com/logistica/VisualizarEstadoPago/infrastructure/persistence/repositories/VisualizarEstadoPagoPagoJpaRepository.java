package com.logistica.VisualizarEstadoPago.infrastructure.persistence.repositories;

import com.logistica.VisualizarEstadoPago.infrastructure.persistence.entities.VisualizarEstadoPagoPagoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VisualizarEstadoPagoPagoJpaRepository extends JpaRepository<VisualizarEstadoPagoPagoEntity, UUID> {
    Optional<VisualizarEstadoPagoPagoEntity> findByIdAndUsuarioId(UUID id, UUID usuarioId);
    List<VisualizarEstadoPagoPagoEntity> findByUsuarioId(UUID usuarioId);
}
