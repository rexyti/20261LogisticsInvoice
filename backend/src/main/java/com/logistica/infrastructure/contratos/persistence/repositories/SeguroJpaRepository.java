package com.logistica.infrastructure.contratos.persistence.repositories;

import com.logistica.infrastructure.contratos.persistence.entities.SeguroEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SeguroJpaRepository extends JpaRepository<SeguroEntity, UUID> {
}
