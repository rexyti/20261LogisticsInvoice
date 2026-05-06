package com.logistica.infrastructure.liquidacion.persistence.repositories;

import com.logistica.infrastructure.liquidacion.persistence.entities.LiquidacionEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LiquidacionJpaRepository extends org.springframework.data.jpa.repository.JpaRepository<LiquidacionEntity, UUID> {
    boolean existsByIdRuta(UUID idRuta);
    Optional<LiquidacionEntity> findByIdRuta(UUID idRuta);
}
