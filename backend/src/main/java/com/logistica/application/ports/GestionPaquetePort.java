package com.logistica.application.ports;

import com.logistica.domain.models.GestionPaquete;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface GestionPaquetePort {
    CompletableFuture<GestionPaquete> consultarEstado(UUID idRuta, UUID idPaquete);
}
