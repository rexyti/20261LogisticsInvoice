package com.logistica.infrastructure.contratos.persistence.repositories;

import com.logistica.infrastructure.contratos.persistence.entities.TransportistaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ContratoTransportistaJpaRepository extends JpaRepository<TransportistaEntity, UUID> {
}
