package com.logistica.domain.repositories;

import com.logistica.domain.models.EventoTransaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EventoRepository extends JpaRepository<EventoTransaccion, UUID> {
}
