package com.logistica.VisualizarLiquidación.infrastructure.persistence.repositories;

import com.logistica.VisualizarLiquidación.infrastructure.persistence.entities.VisualizarLiquidacionRutaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VisualizarLiquidacionRutaJpaRepository extends JpaRepository<VisualizarLiquidacionRutaEntity, UUID> {}
