package com.logistica.application.dtos.response;

import com.logistica.domain.enums.EstadoPaquete;

import java.time.Instant;
import java.util.UUID;

public record HistorialEstadoResponseDTO(UUID id, UUID idPaquete, EstadoPaquete estado, Instant fecha) {}
