package com.logistica.VisualizarEstadoPago.infrastructure.web.controllers;

import com.logistica.VisualizarEstadoPago.application.dtos.response.EstadoPagoResponseDTO;
import com.logistica.VisualizarEstadoPago.application.dtos.response.PagoListDTO;
import com.logistica.VisualizarEstadoPago.application.usecases.pago.ConsultarEstadoPagoUseCase;
import com.logistica.VisualizarEstadoPago.application.usecases.pago.ListarPagosUseCase;
import com.logistica.VisualizarEstadoPago.infrastructure.web.controllers.PagoController;
import com.logistica.VisualizarEstadoPago.domain.enums.EstadoPagoEnum;
import com.logistica.VisualizarEstadoPago.domain.exceptions.AccessDeniedPaymentException;
import com.logistica.VisualizarEstadoPago.domain.exceptions.PagoNoEncontradoException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PagoController.class)
class PagoControllerTest {

    private static final String USUARIO_UUID = "11111111-1111-1111-1111-111111111111";
    private static final UUID USUARIO_ID = UUID.fromString(USUARIO_UUID);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConsultarEstadoPagoUseCase consultarEstadoPagoUseCase;

    @MockBean
    private ListarPagosUseCase listarPagosUseCase;

    @Test
    @WithMockUser(username = USUARIO_UUID)
    void obtenerEstadoPago_CuandoPagoExiste_Retorna200() throws Exception {
        UUID pagoId = UUID.randomUUID();
        EstadoPagoResponseDTO responseDTO = new EstadoPagoResponseDTO(
                pagoId, EstadoPagoEnum.PAGADO.name(), LocalDateTime.now(),
                new BigDecimal("1000.00"), null, UUID.randomUUID());
        when(consultarEstadoPagoUseCase.ejecutar(pagoId, USUARIO_ID)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/pagos/{id}", pagoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pagoId").value(pagoId.toString()))
                .andExpect(jsonPath("$.estado").value("PAGADO"));
    }

    @Test
    @WithMockUser(username = USUARIO_UUID)
    void obtenerEstadoPago_CuandoPagoNoExiste_Retorna404() throws Exception {
        UUID pagoId = UUID.randomUUID();
        when(consultarEstadoPagoUseCase.ejecutar(pagoId, USUARIO_ID))
                .thenThrow(new PagoNoEncontradoException("Pago no encontrado"));

        mockMvc.perform(get("/api/pagos/{id}", pagoId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = USUARIO_UUID)
    void obtenerEstadoPago_CuandoUsuarioNoTienePermiso_Retorna403() throws Exception {
        UUID pagoId = UUID.randomUUID();
        when(consultarEstadoPagoUseCase.ejecutar(pagoId, USUARIO_ID))
                .thenThrow(new AccessDeniedPaymentException("Acceso denegado"));

        mockMvc.perform(get("/api/pagos/{id}", pagoId))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = USUARIO_UUID)
    void listarPagos_CuandoUsuarioTienePagos_Retorna200ConLista() throws Exception {
        PagoListDTO dto = new PagoListDTO(UUID.randomUUID(), UUID.randomUUID(),
                LocalDateTime.now(), new BigDecimal("500.00"), EstadoPagoEnum.PENDIENTE.name());
        when(listarPagosUseCase.ejecutar(USUARIO_ID)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/pagos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));
    }
}
