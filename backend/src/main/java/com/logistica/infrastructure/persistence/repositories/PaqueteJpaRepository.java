package com.logistica.infrastructure.persistence.repositories;

import com.logistica.infrastructure.persistence.entities.PaqueteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaqueteJpaRepository extends JpaRepository<PaqueteEntity, Long> {

    Optional<PaqueteEntity> findByIdPaquete(Long idPaquete);
}
