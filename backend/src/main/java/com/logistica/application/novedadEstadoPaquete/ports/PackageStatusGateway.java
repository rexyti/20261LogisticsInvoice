package com.logistica.application.novedadEstadoPaquete.ports;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PackageStatusGateway {

    CompletableFuture<PackageStatusResult> consultarEstado(UUID idRuta, UUID idPaquete);
}
