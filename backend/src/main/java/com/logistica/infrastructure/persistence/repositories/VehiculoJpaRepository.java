package com.logistica.infrastructure.persistence.repositories;

import com.logistica.infrastructure.persistence.entities.VehiculoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehiculoJpaRepository extends JpaRepository<VehiculoEntity, Long> {
}
