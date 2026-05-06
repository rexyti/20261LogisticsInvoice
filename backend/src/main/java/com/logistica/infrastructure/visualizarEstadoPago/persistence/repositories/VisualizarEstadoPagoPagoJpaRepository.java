package com.logistica.infrastructure.visualizarEstadoPago.persistence.repositories;

import com.logistica.infrastructure.registrarEstadoPago.persistence.entities.RegistrarEstadoPagoPagoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VisualizarEstadoPagoPagoJpaRepository extends JpaRepository<RegistrarEstadoPagoPagoEntity, UUID> {
    Optional<RegistrarEstadoPagoPagoEntity> findByIdPagoAndIdUsuario(UUID idPago, UUID idUsuario);
    List<RegistrarEstadoPagoPagoEntity> findByIdUsuario(UUID idUsuario);
}
