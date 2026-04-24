package com.logistica.infrastructure.web.controllers;

import com.logistica.application.dtos.response.HistorialEstadoResponseDTO;
import com.logistica.application.dtos.response.LogSincronizacionResponseDTO;
import com.logistica.application.dtos.response.SincronizacionResultadoDTO;
import com.logistica.application.usecases.paquete.ObtenerHistorialUseCase;
import com.logistica.application.usecases.paquete.ObtenerLogsSincronizacionUseCase;
import com.logistica.application.usecases.paquete.SincronizarPaqueteUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PaqueteController {

    private final SincronizarPaqueteUseCase sincronizarUseCase;
    private final ObtenerHistorialUseCase historialUseCase;
    private final ObtenerLogsSincronizacionUseCase logsUseCase;

    /**
     * Disparado por el proceso de liquidación para sincronizar el estado de un paquete específico.
     * El usuario no invoca este endpoint directamente.
     */
    @PostMapping("/rutas/{idRuta}/paquetes/{idPaquete}/sincronizar")
    public ResponseEntity<SincronizacionResultadoDTO> sincronizar(
            @PathVariable UUID idRuta,
            @PathVariable UUID idPaquete) {
        return ResponseEntity.ok(sincronizarUseCase.sincronizarEstado(idRuta, idPaquete));
    }

    /**
     * Devuelve el historial paginado de estados para un paquete (FR-003, FR-004).
     */
    @GetMapping("/paquetes/{idPaquete}/historial")
    public ResponseEntity<Page<HistorialEstadoResponseDTO>> historial(
            @PathVariable UUID idPaquete,
            @PageableDefault(size = 20, sort = "fecha") Pageable pageable) {
        return ResponseEntity.ok(historialUseCase.obtenerHistorial(idPaquete, pageable));
    }

    /**
     * Devuelve los logs de sincronización para un paquete (SC-002).
     */
    @GetMapping("/paquetes/{idPaquete}/logs")
    public ResponseEntity<List<LogSincronizacionResponseDTO>> logs(@PathVariable UUID idPaquete) {
        return ResponseEntity.ok(logsUseCase.obtenerLogs(idPaquete));
    }
}
