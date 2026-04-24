package com.logistica.infrastructure.web.controllers;

import com.logistica.application.dtos.response.HistorialEstadoDTO;
import com.logistica.application.dtos.response.LogSincronizacionDTO;
import com.logistica.application.dtos.response.SincronizacionResultadoDTO;
import com.logistica.application.usecases.paquete.ObtenerHistorialUseCase;
import com.logistica.application.usecases.paquete.ObtenerLogsSincronizacionUseCase;
import com.logistica.application.usecases.paquete.SincronizarPaqueteUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<HistorialEstadoDTO>> historial(@PathVariable Long idPaquete) {
        return ResponseEntity.ok(historialUseCase.execute(idPaquete));
    }

    /** Returns all sync audit logs (for financial team review). */
    @GetMapping("/sincronizacion/logs")
    public ResponseEntity<List<LogSincronizacionDTO>> logs() {
        return ResponseEntity.ok(logsUseCase.findAll());
    }

    /** Returns sync logs filtered by package. */
    @GetMapping("/sincronizacion/logs/{idPaquete}")
    public ResponseEntity<List<LogSincronizacionDTO>> logsPorPaquete(@PathVariable Long idPaquete) {
        return ResponseEntity.ok(logsUseCase.findByIdPaquete(idPaquete));
    }
}
