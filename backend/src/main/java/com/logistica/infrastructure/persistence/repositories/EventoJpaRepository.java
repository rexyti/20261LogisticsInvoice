package com.logistica.infrastructure.persistence.repositories;

import com.logistica.infrastructure.persistence.entities.EventoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EventoJpaRepository extends JpaRepository<EventoEntity, UUID> {
}
