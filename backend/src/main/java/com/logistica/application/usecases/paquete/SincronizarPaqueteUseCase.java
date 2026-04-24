package com.logistica.application.usecases.paquete;

import com.logistica.application.dtos.response.SincronizacionResultadoDTO;

import java.util.UUID;

public interface SincronizarPaqueteUseCase {
    SincronizacionResultadoDTO sincronizarEstado(UUID idRuta, UUID idPaquete);
}
