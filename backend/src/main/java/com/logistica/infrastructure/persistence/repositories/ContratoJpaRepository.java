package com.logistica.infrastructure.persistence.repositories;

import com.logistica.infrastructure.persistence.entities.ContratoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ContratoJpaRepository extends JpaRepository<ContratoEntity, UUID> {
}
