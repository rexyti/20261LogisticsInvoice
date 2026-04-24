package com.logistica.domain.repositories;

import com.logistica.domain.models.EstadoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EstadoPagoRepository extends JpaRepository<EstadoPago, UUID> {
}
