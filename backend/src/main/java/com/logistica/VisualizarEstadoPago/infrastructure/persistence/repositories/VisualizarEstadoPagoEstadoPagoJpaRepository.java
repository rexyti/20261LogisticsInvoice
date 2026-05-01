package com.logistica.VisualizarEstadoPago.infrastructure.persistence.repositories;

import com.logistica.VisualizarEstadoPago.infrastructure.persistence.entities.VisualizarEstadoPagoEstadoPagoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VisualizarEstadoPagoEstadoPagoJpaRepository extends JpaRepository<VisualizarEstadoPagoEstadoPagoEntity, UUID> {
}
