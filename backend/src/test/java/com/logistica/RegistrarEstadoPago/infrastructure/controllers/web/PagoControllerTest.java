package com.logistica.RegistrarEstadoPago.infrastructure.controllers.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistica.RegistrarEstadoPago.application.dtos.response.EventoTransaccionResponseDTO;
import com.logistica.RegistrarEstadoPago.application.dtos.response.PagoResponseDTO;
import com.logistica.RegistrarEstadoPago.application.usecases.pago.ObtenerEstadoPagoUseCase;
import com.logistica.RegistrarEstadoPago.application.usecases.pago.ObtenerEventosTransaccionUseCase;
import com.logistica.RegistrarEstadoPago.infrastructure.web.controllers.RegistrarEstadoPagoPagoController;
import com.logistica.RegistrarEstadoPago.domain.enums.EstadoEventoTransaccion;
import com.logistica.RegistrarEstadoPago.domain.enums.RegistrarEstadoPagoEstadoPagoEnum;
import com.logistica.RegistrarEstadoPago.exceptions.RegistrarEstadoPagoPagoNoEncontradoException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegistrarEstadoPagoPagoController.class)
class PagoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ObtenerEstadoPagoUseCase obtenerEstadoPagoUseCase;

    @MockBean
    private ObtenerEventosTransaccionUseCase obtenerEventosTransaccionUseCase;

    @Test
    void get_estadoPago_existente_retorna200() throws Exception {
        UUID idPago = UUID.randomUUID();
        PagoResponseDTO response = new PagoResponseDTO(idPago, UUID.randomUUID(),
                RegistrarEstadoPagoEstadoPagoEnum.PAGADO, Instant.now(), 2L);
        when(obtenerEstadoPagoUseCase.obtenerEstadoPago(any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/pagos/{idPago}/estado", idPago))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("PAGADO"))
                .andExpect(jsonPath("$.ultimaSecuenciaProcesada").value(2));
    }

    @Test
    void get_estadoPago_pagoInexistente_retorna404() throws Exception {
        UUID idPago = UUID.randomUUID();
        when(obtenerEstadoPagoUseCase.obtenerEstadoPago(any()))
                .thenThrow(new RegistrarEstadoPagoPagoNoEncontradoException(idPago.toString()));

        mockMvc.perform(get("/api/v1/pagos/{idPago}/estado", idPago))
                .andExpect(status().isNotFound());
    }

    @Test
    void get_eventos_existentes_retorna200() throws Exception {
        UUID idPago = UUID.randomUUID();
        UUID idLiquidacion = UUID.randomUUID();
        List<EventoTransaccionResponseDTO> eventos = List.of(
                new EventoTransaccionResponseDTO(UUID.randomUUID(), "txn-001", idPago, idLiquidacion,
                        RegistrarEstadoPagoEstadoPagoEnum.EN_PROCESO, EstadoEventoTransaccion.PROCESADO,
                        Instant.now(), Instant.now(), 1L, null)
        );
        when(obtenerEventosTransaccionUseCase.obtenerEventos(any())).thenReturn(eventos);

        mockMvc.perform(get("/api/v1/pagos/{idPago}/eventos", idPago))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idTransaccionBanco").value("txn-001"))
                .andExpect(jsonPath("$[0].estadoProcesamiento").value("PROCESADO"));
    }

    @Test
    void get_eventos_sinEventos_retorna200ConListaVacia() throws Exception {
        UUID idPago = UUID.randomUUID();
        when(obtenerEventosTransaccionUseCase.obtenerEventos(any())).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/pagos/{idPago}/eventos", idPago))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}
