package com.logistica.infrastructure.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistica.application.dtos.response.RecepcionEventoPagoResponseDTO;
import com.logistica.application.usecases.pago.RecibirEventoPagoUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WebhookPagoController.class)
class WebhookPagoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RecibirEventoPagoUseCase recibirEventoPagoUseCase;

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
                .andExpect(jsonPath("$.idEvento").value("evt-001"))
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
                .andExpect(jsonPath("$.idEvento").exists())
                .andExpect(jsonPath("$.procesamiento").exists());
    }

    private Map<String, Object> buildRequestBody(String estado) {
        return Map.of(
                "idEvento", "evt-001",
                "idTransaccionBanco", "txn-001",
                "idPago", UUID.randomUUID().toString(),
                "idLiquidacion", UUID.randomUUID().toString(),
                "estado", estado,
                "fechaEvento", "2026-04-26T10:30:00"
        );
    }

    private Map<String, Object> buildRequestBodyConEstado(String estado) {
        return Map.of(
                "idEvento", "evt-005",
                "idTransaccionBanco", "txn-005",
                "idPago", UUID.randomUUID().toString(),
                "idLiquidacion", UUID.randomUUID().toString(),
                "estado", estado,
                "fechaEvento", "2026-04-26T10:50:00"
        );
    }
}
