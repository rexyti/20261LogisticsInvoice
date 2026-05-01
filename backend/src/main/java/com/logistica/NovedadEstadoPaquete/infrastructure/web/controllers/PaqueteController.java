package com.logistica.NovedadEstadoPaquete.infrastructure.web.controllers;

import com.logistica.NovedadEstadoPaquete.application.dtos.response.HistorialEstadoDTO;
import com.logistica.NovedadEstadoPaquete.application.dtos.response.LogSincronizacionDTO;
import com.logistica.NovedadEstadoPaquete.application.dtos.response.SincronizacionResultadoDTO;
import com.logistica.NovedadEstadoPaquete.application.usecases.paquete.ObtenerHistorialUseCase;
import com.logistica.NovedadEstadoPaquete.application.usecases.paquete.ObtenerLogsSincronizacionUseCase;
import com.logistica.NovedadEstadoPaquete.application.usecases.paquete.SincronizarPaqueteUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;



@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaqueteController {

    private final SincronizarPaqueteUseCase        sincronizarUseCase;
    private final ObtenerHistorialUseCase           historialUseCase;
    private final ObtenerLogsSincronizacionUseCase  logsUseCase;

    /**
     * Triggers synchronous consultation with the Package Management Module.
     * Called automatically by the liquidation process; also exposed for integration testing.
     * FR-001: GET /route/{idRoute}/package/{idPaquete} is the external call made inside.
     */
    @PostMapping("/rutas/{idRuta}/paquetes/{idPaquete}/sincronizar")
    public ResponseEntity<SincronizacionResultadoDTO> sincronizar(
            @PathVariable Long idRuta,
            @PathVariable Long idPaquete) {
        return ResponseEntity.ok(sincronizarUseCase.execute(idRuta, idPaquete));
    }

    /** Returns state history for a package ordered by fecha DESC. */
    @GetMapping("/paquetes/{idPaquete}/historial")
    public ResponseEntity<List<HistorialEstadoDTO>> obtenerHistorial(
            @PathVariable Long idPaquete,
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
            @PathVariable Long idPaquete,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return ResponseEntity.ok(logsUseCase.findByIdPaquete(idPaquete, page, size));
    }
}
