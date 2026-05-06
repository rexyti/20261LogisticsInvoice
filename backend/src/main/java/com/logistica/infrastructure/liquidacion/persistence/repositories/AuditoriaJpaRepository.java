package com.logistica.infrastructure.liquidacion.persistence.repositories;

import com.logistica.infrastructure.liquidacion.persistence.entities.AuditoriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuditoriaJpaRepository extends JpaRepository<AuditoriaEntity, UUID> {
    List<AuditoriaEntity> findByIdLiquidacion(UUID idLiquidacion);
}
