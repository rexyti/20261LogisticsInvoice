package com.logistica.domain.models;

import com.logistica.domain.enums.EstadoPaquete;

import java.util.UUID;

public record Paquete(UUID idPaquete, UUID idRuta, EstadoPaquete estadoActual) {}
