package com.logistica.RegistrarEstadoPago.infrastructure.controllers.web;

import com.logistica.application.registrarEstadoPago.dtos.response.PagoResponseDTO;
import com.logistica.application.registrarEstadoPago.usecases.pago.ObtenerEstadoPagoUseCase;
import com.logistica.domain.registrarEstadoPago.enums.RegistrarEstadoPagoEstadoPagoEnum;
import com.logistica.domain.registrarEstadoPago.exceptions.RegistrarEstadoPagoPagoNoEncontradoException;
import com.logistica.infrastructure.registrarEstadoPago.web.controllers.RegistrarEstadoPagoLiquidacionController;
import com.logistica.infrastructure.shared.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegistrarEstadoPagoLiquidacionController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser
class LiquidacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ObtenerEstadoPagoUseCase obtenerEstadoPagoUseCase;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void get_estadoPagoPorLiquidacion_existente_retorna200() throws Exception {
        UUID idLiquidacion = UUID.randomUUID();
        UUID idPago = UUID.randomUUID();
        PagoResponseDTO response = new PagoResponseDTO(idPago, idLiquidacion,
                RegistrarEstadoPagoEstadoPagoEnum.PAGADO, Instant.now(), 3L);
        when(obtenerEstadoPagoUseCase.obtenerEstadoPagoPorLiquidacion(any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/liquidaciones/{idLiquidacion}/pago/estado", idLiquidacion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("PAGADO"))
                .andExpect(jsonPath("$.id_pago").value(idPago.toString()));
    }

    @Test
    void get_estadoPagoPorLiquidacion_sinPago_retorna404() throws Exception {
        UUID idLiquidacion = UUID.randomUUID();
        when(obtenerEstadoPagoUseCase.obtenerEstadoPagoPorLiquidacion(any()))
                .thenThrow(new RegistrarEstadoPagoPagoNoEncontradoException("liquidacion:" + idLiquidacion));

        mockMvc.perform(get("/api/v1/liquidaciones/{idLiquidacion}/pago/estado", idLiquidacion))
                .andExpect(status().isNotFound());
    }
}
