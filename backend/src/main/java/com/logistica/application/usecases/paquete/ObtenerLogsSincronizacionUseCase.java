package com.logistica.application.usecases.paquete;

import com.logistica.application.dtos.response.LogSincronizacionResponseDTO;

import java.util.List;
import java.util.UUID;

public interface ObtenerLogsSincronizacionUseCase {
    List<LogSincronizacionResponseDTO> obtenerLogs(UUID idPaquete);
}
