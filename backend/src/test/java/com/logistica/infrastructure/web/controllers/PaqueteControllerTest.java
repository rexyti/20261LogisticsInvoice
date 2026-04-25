package com.logistica.infrastructure.web.controllers;

import com.logistica.application.dtos.response.HistorialEstadoResponseDTO;
import com.logistica.application.dtos.response.LogSincronizacionResponseDTO;
import com.logistica.application.dtos.response.SincronizacionResultadoDTO;
import com.logistica.application.usecases.paquete.ObtenerHistorialUseCase;
import com.logistica.application.usecases.paquete.ObtenerLogsSincronizacionUseCase;
import com.logistica.application.usecases.paquete.SincronizarPaqueteUseCase;
import com.logistica.domain.enums.EstadoPaquete;
import com.logistica.shared.exceptions.PaqueteNoEncontradoException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaqueteController.class)
class PaqueteControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean SincronizarPaqueteUseCase sincronizarUseCase;
    @MockBean ObtenerHistorialUseCase historialUseCase;
    @MockBean ObtenerLogsSincronizacionUseCase logsUseCase;

    // ── POST /api/v1/rutas/{idRuta}/paquetes/{idPaquete}/sincronizar ──────────

    @Test
    void sincronizar_estadoExitoso_return200ConResultado() throws Exception {
        UUID idRuta    = UUID.randomUUID();
        UUID idPaquete = UUID.randomUUID();

        when(sincronizarUseCase.sincronizarEstado(idRuta, idPaquete))
                .thenReturn(SincronizacionResultadoDTO.exitoso(100, "ENTREGADO"));

        mockMvc.perform(post("/api/v1/rutas/{idRuta}/paquetes/{idPaquete}/sincronizar",
                        idRuta, idPaquete))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ENTREGADO"))
                .andExpect(jsonPath("$.porcentajePago").value(100))
                .andExpect(jsonPath("$.mensaje").value("Sincronización exitosa"));
    }

    @Test
    void sincronizar_paqueteNoEncontrado_return200ConEstadoNoEncontrado() throws Exception {
        UUID idRuta    = UUID.randomUUID();
        UUID idPaquete = UUID.randomUUID();

        when(sincronizarUseCase.sincronizarEstado(idRuta, idPaquete))
                .thenReturn(SincronizacionResultadoDTO.noEncontrado(idPaquete.toString()));

        mockMvc.perform(post("/api/v1/rutas/{idRuta}/paquetes/{idPaquete}/sincronizar",
                        idRuta, idPaquete))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("PAQUETE_NO_ENCONTRADO"));
    }

    @Test
    void sincronizar_timeout_return200ConEstadoPendiente() throws Exception {
        UUID idRuta    = UUID.randomUUID();
        UUID idPaquete = UUID.randomUUID();

        when(sincronizarUseCase.sincronizarEstado(idRuta, idPaquete))
                .thenReturn(SincronizacionResultadoDTO.pendiente(idPaquete.toString()));

        mockMvc.perform(post("/api/v1/rutas/{idRuta}/paquetes/{idPaquete}/sincronizar",
                        idRuta, idPaquete))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("PENDIENTE_SINCRONIZACION"));
    }

    // El GlobalExceptionHandler convierte PaqueteNoEncontradoException en 404
    @Test
    void sincronizar_excepcionPaqueteNoEncontrado_return404() throws Exception {
        UUID idRuta    = UUID.randomUUID();
        UUID idPaquete = UUID.randomUUID();

        when(sincronizarUseCase.sincronizarEstado(idRuta, idPaquete))
                .thenThrow(new PaqueteNoEncontradoException(idPaquete));

        mockMvc.perform(post("/api/v1/rutas/{idRuta}/paquetes/{idPaquete}/sincronizar",
                        idRuta, idPaquete))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Paquete no encontrado"))
                .andExpect(jsonPath("$.idPaquete").value(idPaquete.toString()));
    }

    // ── GET /api/v1/paquetes/{idPaquete}/historial ────────────────────────────

    @Test
    void historial_paqueteConRegistros_return200ConPaginado() throws Exception {
        UUID idPaquete = UUID.randomUUID();
        HistorialEstadoResponseDTO dto = new HistorialEstadoResponseDTO(
                UUID.randomUUID(), idPaquete, EstadoPaquete.ENTREGADO, Instant.now());

        when(historialUseCase.obtenerHistorial(eq(idPaquete), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(dto)));

        mockMvc.perform(get("/api/v1/paquetes/{idPaquete}/historial", idPaquete))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].estado").value("ENTREGADO"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void historial_sinRegistros_return200PaginaVacia() throws Exception {
        UUID idPaquete = UUID.randomUUID();

        when(historialUseCase.obtenerHistorial(eq(idPaquete), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/v1/paquetes/{idPaquete}/historial", idPaquete))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    // ── GET /api/v1/paquetes/{idPaquete}/logs ─────────────────────────────────

    @Test
    void logs_paqueteConLogs_return200ConLista() throws Exception {
        UUID idPaquete = UUID.randomUUID();
        LogSincronizacionResponseDTO dto = new LogSincronizacionResponseDTO(
                UUID.randomUUID(), idPaquete, 200, "{\"estado\":\"ENTREGADO\"}", Instant.now());

        when(logsUseCase.obtenerLogs(idPaquete)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/paquetes/{idPaquete}/logs", idPaquete))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigoRespuestaHTTP").value(200));
    }

    @Test
    void logs_sinLogs_return200ListaVacia() throws Exception {
        UUID idPaquete = UUID.randomUUID();

        when(logsUseCase.obtenerLogs(idPaquete)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/paquetes/{idPaquete}/logs", idPaquete))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}
