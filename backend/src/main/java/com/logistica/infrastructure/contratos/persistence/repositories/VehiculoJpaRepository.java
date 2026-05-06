package com.logistica.infrastructure.contratos.persistence.repositories;

import com.logistica.infrastructure.contratos.persistence.entities.VehiculoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VehiculoJpaRepository extends JpaRepository<VehiculoEntity, UUID> {
}
