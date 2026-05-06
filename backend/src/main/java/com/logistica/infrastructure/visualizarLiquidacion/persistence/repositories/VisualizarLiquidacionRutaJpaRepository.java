package com.logistica.infrastructure.visualizarLiquidacion.persistence.repositories;

import com.logistica.infrastructure.visualizarLiquidacion.persistence.entities.VisualizarLiquidacionRutaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VisualizarLiquidacionRutaJpaRepository extends JpaRepository<VisualizarLiquidacionRutaEntity, UUID> {}
