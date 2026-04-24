package com.logistica.application.usecases.paquete;

import com.logistica.application.dtos.response.HistorialEstadoResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ObtenerHistorialUseCase {
    Page<HistorialEstadoResponseDTO> obtenerHistorial(UUID idPaquete, Pageable pageable);
}
