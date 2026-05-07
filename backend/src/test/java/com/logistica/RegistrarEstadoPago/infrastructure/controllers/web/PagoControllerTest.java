package com.logistica.RegistrarEstadoPago.infrastructure.controllers.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistica.application.registrarEstadoPago.dtos.response.EventoTransaccionResponseDTO;
import com.logistica.application.registrarEstadoPago.dtos.response.PagoResponseDTO;
import com.logistica.application.registrarEstadoPago.usecases.pago.ObtenerEstadoPagoUseCase;
import com.logistica.application.registrarEstadoPago.usecases.pago.ObtenerEventosTransaccionUseCase;
import com.logistica.domain.registrarEstadoPago.enums.EstadoEventoTransaccion;
import com.logistica.domain.registrarEstadoPago.enums.RegistrarEstadoPagoEstadoPagoEnum;
import com.logistica.domain.registrarEstadoPago.exceptions.RegistrarEstadoPagoPagoNoEncontradoException;
import com.logistica.infrastructure.registrarEstadoPago.web.controllers.RegistrarEstadoPagoPagoController;
import com.logistica.infrastructure.shared.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
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
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser
class PagoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ObtenerEstadoPagoUseCase obtenerEstadoPagoUseCase;

    @MockBean
    private ObtenerEventosTransaccionUseCase obtenerEventosTransaccionUseCase;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void get_estadoPago_existente_retorna200() throws Exception {
        UUID idPago = UUID.randomUUID();
        PagoResponseDTO response = new PagoResponseDTO(idPago, UUID.randomUUID(),
                RegistrarEstadoPagoEstadoPagoEnum.PAGADO, Instant.now(), 2L);
        when(obtenerEstadoPagoUseCase.obtenerEstadoPago(any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/pagos/{idPago}/estado", idPago))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("PAGADO"))
                .andExpect(jsonPath("$.ultima_secuencia_procesada").value(2));
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
                .andExpect(jsonPath("$[0].id_transaccion_banco").value("txn-001"))
                .andExpect(jsonPath("$[0].estado_procesamiento").value("PROCESADO"));
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
