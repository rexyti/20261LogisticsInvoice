package com.logistica.application.dtos.response;

import com.logistica.domain.enums.EstadoPaquete;

import java.util.UUID;

public record PaqueteResponseDTO(UUID idPaquete, UUID idRuta, EstadoPaquete estadoActual) {}
