package com.logistica.liquidacion.infrastructure.persistence.repositories;

import com.logistica.liquidacion.infrastructure.persistence.entities.AjusteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AjusteJpaRepository extends JpaRepository<AjusteEntity, UUID> {
    List<AjusteEntity> findByLiquidacion_Id(UUID liquidacionId);
}
