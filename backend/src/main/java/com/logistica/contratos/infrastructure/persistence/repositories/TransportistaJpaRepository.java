package com.logistica.contratos.infrastructure.persistence.repositories;

import com.logistica.contratos.infrastructure.persistence.entities.TransportistaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransportistaJpaRepository extends JpaRepository<TransportistaEntity, UUID> {
}
