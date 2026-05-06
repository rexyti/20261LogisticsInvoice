package com.logistica.infrastructure.registrarEstadoPago.persistence.repositories;

import com.logistica.domain.registrarEstadoPago.enums.EstadoEventoTransaccion;
import com.logistica.infrastructure.registrarEstadoPago.persistence.entities.EventoTransaccionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventoTransaccionJpaRepository extends JpaRepository<EventoTransaccionEntity, UUID> {
    Optional<EventoTransaccionEntity> findByIdTransaccionBanco(String idTransaccionBanco);
    List<EventoTransaccionEntity> findByIdPagoOrderByFechaRecepcionAsc(UUID idPago);
    List<EventoTransaccionEntity> findByIdPagoAndEstadoProcesamiento(UUID idPago, EstadoEventoTransaccion estado);
}
