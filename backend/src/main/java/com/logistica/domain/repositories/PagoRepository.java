package com.logistica.domain.repositories;

import com.logistica.domain.models.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PagoRepository extends JpaRepository<Pago, UUID> {

    Optional<Pago> findByIdAndUsuarioId(UUID id, UUID usuarioId);

    List<Pago> findByUsuarioId(UUID usuarioId);
}
