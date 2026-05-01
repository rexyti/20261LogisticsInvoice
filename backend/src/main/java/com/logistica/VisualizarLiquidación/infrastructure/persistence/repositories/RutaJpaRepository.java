package com.logistica.VisualizarLiquidación.infrastructure.persistence.repositories;

import com.logistica.VisualizarLiquidación.infrastructure.persistence.entities.RutaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RutaJpaRepository extends JpaRepository<RutaEntity, UUID> {}
