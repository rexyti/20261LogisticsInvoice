package com.logistica.infrastructure.persistence.repositories;

import com.logistica.infrastructure.persistence.entities.PagoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PagoJpaRepository extends JpaRepository<PagoEntity, UUID> {
    Optional<PagoEntity> findByIdAndUsuarioId(UUID id, UUID usuarioId);
    List<PagoEntity> findByUsuarioId(UUID usuarioId);
}
