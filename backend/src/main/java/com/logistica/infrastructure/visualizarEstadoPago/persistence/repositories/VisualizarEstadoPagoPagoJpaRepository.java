package com.logistica.infrastructure.visualizarEstadoPago.persistence.repositories;

import com.logistica.infrastructure.visualizarEstadoPago.persistence.entities.VisualizarEstadoPagoPagoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VisualizarEstadoPagoPagoJpaRepository extends JpaRepository<VisualizarEstadoPagoPagoEntity, UUID> {
    Optional<VisualizarEstadoPagoPagoEntity> findByIdPagoAndUsuarioId(UUID idPago, UUID usuarioId);
    List<VisualizarEstadoPagoPagoEntity> findByUsuarioId(UUID usuarioId);
}
