package com.logistica.VisualizarLiquidación.infrastructure.persistence.repositories;

import com.logistica.VisualizarLiquidación.infrastructure.persistence.entities.VisualizarLiquidacionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VisualizarLiquidacionJpaRepository extends JpaRepository<VisualizarLiquidacionEntity, UUID> {

    Page<VisualizarLiquidacionEntity> findByUsuarioId(String usuarioId, Pageable pageable);

    Optional<VisualizarLiquidacionEntity> findByRuta_Id(UUID idRuta);

    boolean existsByRuta_Id(UUID idRuta);
}
