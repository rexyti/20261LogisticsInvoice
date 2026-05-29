package com.logistica.infrastructure.novedadEstadoPaquete.web.controllers;

import com.logistica.application.novedadEstadoPaquete.dtos.response.HistorialEstadoDTO;
import com.logistica.application.novedadEstadoPaquete.dtos.response.LogSincronizacionDTO;
import com.logistica.application.novedadEstadoPaquete.usecases.paquete.PaqueteObtenerHistorialUseCase;
import com.logistica.application.novedadEstadoPaquete.usecases.paquete.ObtenerLogsSincronizacionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaqueteController {

    private final PaqueteObtenerHistorialUseCase historialUseCase;
    private final ObtenerLogsSincronizacionUseCase logsUseCase;

    @GetMapping("/paquetes/{idPaquete}/historial")
    public ResponseEntity<List<HistorialEstadoDTO>> obtenerHistorial(
            @PathVariable UUID idPaquete,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return ResponseEntity.ok(historialUseCase.execute(idPaquete, page, size));
    }

    @GetMapping("/sincronizacion/logs")
    public ResponseEntity<List<LogSincronizacionDTO>> obtenerLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return ResponseEntity.ok(logsUseCase.findAll(page, size));
    }

    @GetMapping("/sincronizacion/logs/paquetes/{idPaquete}")
    public ResponseEntity<List<LogSincronizacionDTO>> obtenerLogsPorPaquete(
            @PathVariable UUID idPaquete,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return ResponseEntity.ok(logsUseCase.findByIdPaquete(idPaquete, page, size));
    }
}
