package com.logistica.liquidacion.infrastructure.persistence.repositories;

import com.logistica.liquidacion.infrastructure.persistence.entities.LiquidacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LiquidacionJpaRepository extends JpaRepository<LiquidacionEntity, UUID> {
    boolean existsByIdRuta(UUID idRuta);
    Optional<LiquidacionEntity> findByIdRuta(UUID idRuta);
}
