package com.logistica.infrastructure.visualizarEstadoPago.persistence.repositories;

import com.logistica.infrastructure.registrarEstadoPago.persistence.entities.RegistrarEstadoPagoEstadoPagoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VisualizarEstadoPagoEstadoPagoJpaRepository extends JpaRepository<RegistrarEstadoPagoEstadoPagoEntity, UUID> {
}
