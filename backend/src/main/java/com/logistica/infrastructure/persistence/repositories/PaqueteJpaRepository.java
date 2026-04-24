package com.logistica.infrastructure.persistence.repositories;

import com.logistica.infrastructure.persistence.entities.PaqueteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaqueteJpaRepository extends JpaRepository<PaqueteEntity, UUID> {}
