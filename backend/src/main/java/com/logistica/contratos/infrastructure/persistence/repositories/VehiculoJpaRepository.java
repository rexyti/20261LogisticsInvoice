package com.logistica.contratos.infrastructure.persistence.repositories;

import com.logistica.contratos.infrastructure.persistence.entities.VehiculoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VehiculoJpaRepository extends JpaRepository<VehiculoEntity, UUID> {
}
