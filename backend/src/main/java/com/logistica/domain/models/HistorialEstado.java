package com.logistica.domain.models;

import com.logistica.domain.enums.EstadoPaquete;

import java.time.Instant;
import java.util.UUID;

public record HistorialEstado(UUID id, UUID idPaquete, EstadoPaquete estado, Instant fecha) {}
