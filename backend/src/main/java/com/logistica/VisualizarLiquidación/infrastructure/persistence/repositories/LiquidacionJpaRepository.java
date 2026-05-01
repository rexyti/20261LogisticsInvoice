package com.logistica.VisualizarLiquidación.infrastructure.persistence.repositories;

import com.logistica.VisualizarLiquidación.infrastructure.persistence.entities.LiquidacionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LiquidacionJpaRepository extends JpaRepository<LiquidacionEntity, UUID> {

    Page<LiquidacionEntity> findByUsuarioId(String usuarioId, Pageable pageable);

    Optional<LiquidacionEntity> findByRuta_Id(UUID idRuta);

    boolean existsByRuta_Id(UUID idRuta);
}
