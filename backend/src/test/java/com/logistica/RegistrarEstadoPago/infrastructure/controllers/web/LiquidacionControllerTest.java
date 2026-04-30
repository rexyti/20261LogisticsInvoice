package com.logistica.RegistrarEstadoPago.infrastructure.controllers.web;

import com.logistica.RegistrarEstadoPago.application.dtos.response.PagoResponseDTO;
import com.logistica.RegistrarEstadoPago.application.usecases.pago.ObtenerEstadoPagoUseCase;
import com.logistica.RegistrarEstadoPago.infrastructure.web.controllers.LiquidacionController;
import com.logistica.RegistrarEstadoPago.domain.enums.EstadoPagoEnum;
import com.logistica.RegistrarEstadoPago.exceptions.PagoNoEncontradoException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LiquidacionController.class)
class LiquidacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ObtenerEstadoPagoUseCase obtenerEstadoPagoUseCase;

    @Test
    void get_estadoPagoPorLiquidacion_existente_retorna200() throws Exception {
        UUID idLiquidacion = UUID.randomUUID();
        UUID idPago = UUID.randomUUID();
        PagoResponseDTO response = new PagoResponseDTO(idPago, idLiquidacion,
                EstadoPagoEnum.PAGADO, Instant.now(), 3L);
        when(obtenerEstadoPagoUseCase.obtenerEstadoPagoPorLiquidacion(any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/liquidaciones/{idLiquidacion}/pago/estado", idLiquidacion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("PAGADO"))
                .andExpect(jsonPath("$.idPago").value(idPago.toString()));
    }

    @Test
    void get_estadoPagoPorLiquidacion_sinPago_retorna404() throws Exception {
        UUID idLiquidacion = UUID.randomUUID();
        when(obtenerEstadoPagoUseCase.obtenerEstadoPagoPorLiquidacion(any()))
                .thenThrow(new PagoNoEncontradoException("liquidacion:" + idLiquidacion));

        mockMvc.perform(get("/api/v1/liquidaciones/{idLiquidacion}/pago/estado", idLiquidacion))
                .andExpect(status().isNotFound());
    }
}
