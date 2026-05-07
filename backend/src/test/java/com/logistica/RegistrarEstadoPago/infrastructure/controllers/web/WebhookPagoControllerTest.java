package com.logistica.RegistrarEstadoPago.infrastructure.controllers.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistica.application.registrarEstadoPago.dtos.response.RecepcionEventoPagoResponseDTO;
import com.logistica.application.registrarEstadoPago.usecases.pago.RecibirEventoPagoUseCase;
import com.logistica.infrastructure.registrarEstadoPago.web.controllers.WebhookPagoController;
import com.logistica.infrastructure.shared.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WebhookPagoController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser
class WebhookPagoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RecibirEventoPagoUseCase recibirEventoPagoUseCase;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void post_eventoValido_retorna202() throws Exception {
        when(recibirEventoPagoUseCase.recibirEvento(any()))
                .thenReturn(new RecepcionEventoPagoResponseDTO(
                        "Evento de pago recibido correctamente",
                        "evt-001", "txn-001", "ASINCRONO"));

        Map<String, Object> body = buildRequestBody("EN_PROCESO");

        mockMvc.perform(post("/api/v1/pagos/webhook/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.mensaje").value("Evento de pago recibido correctamente"))
                .andExpect(jsonPath("$.id_evento").value("evt-001"))
                .andExpect(jsonPath("$.procesamiento").value("ASINCRONO"));
    }

    @Test
    void post_sinCamposObligatorios_retorna400() throws Exception {
        Map<String, Object> body = Map.of("estado", "EN_PROCESO");

        mockMvc.perform(post("/api/v1/pagos/webhook/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void post_estadoDesconocido_retorna400() throws Exception {
        Map<String, Object> body = buildRequestBodyConEstado("APROBADO_PARCIALMENTE");

        mockMvc.perform(post("/api/v1/pagos/webhook/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void post_respuestaNoExponeEntidadesInternas() throws Exception {
        when(recibirEventoPagoUseCase.recibirEvento(any()))
                .thenReturn(new RecepcionEventoPagoResponseDTO(
                        "Evento de pago recibido correctamente",
                        "evt-001", "txn-001", "ASINCRONO"));

        Map<String, Object> body = buildRequestBody("PAGADO");

        mockMvc.perform(post("/api/v1/pagos/webhook/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.mensaje").exists())
                .andExpect(jsonPath("$.id_evento").exists())
                .andExpect(jsonPath("$.procesamiento").exists());
    }

    private Map<String, Object> buildRequestBody(String estado) {
        return Map.of(
                "id_evento", "evt-001",
                "id_transaccion_banco", "txn-001",
                "id_pago", UUID.randomUUID().toString(),
                "id_liquidacion", UUID.randomUUID().toString(),
                "estado", estado,
                "fecha_evento", "2026-04-26T10:30:00"
        );
    }

    private Map<String, Object> buildRequestBodyConEstado(String estado) {
        return Map.of(
                "id_evento", "evt-005",
                "id_transaccion_banco", "txn-005",
                "id_pago", UUID.randomUUID().toString(),
                "id_liquidacion", UUID.randomUUID().toString(),
                "estado", estado,
                "fecha_evento", "2026-04-26T10:50:00"
        );
    }
}
