package com.logistica.infrastructure.persistence.repositories;

import com.logistica.infrastructure.persistence.entities.PagoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PagoJpaRepository extends JpaRepository<PagoEntity, UUID> {
    List<PagoEntity> findByIdLiquidacion(UUID idLiquidacion);
}
