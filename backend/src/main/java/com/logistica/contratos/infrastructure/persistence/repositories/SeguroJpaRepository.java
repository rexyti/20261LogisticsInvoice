package com.logistica.contratos.infrastructure.persistence.repositories;

import com.logistica.contratos.infrastructure.persistence.entities.SeguroEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SeguroJpaRepository extends JpaRepository<SeguroEntity, UUID> {
}
