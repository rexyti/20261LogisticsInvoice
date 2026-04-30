package com.logistica.VisualizarEstadoPago.infrastructure.web.controllers;

import com.logistica.VisualizarEstadoPago.infrastructure.web.controllers.PagoController;
import com.logistica.VisualizarEstadoPago.application.usecases.pago.ConsultarEstadoPagoUseCase;
import com.logistica.VisualizarEstadoPago.application.dtos.response.EstadoPagoResponseDTO;
import com.logistica.VisualizarEstadoPago.domain.enums.EstadoPagoEnum;
import com.logistica.VisualizarEstadoPago.domain.exceptions.PagoNoEncontradoException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PagoController.class)
@AutoConfigureMockMvc(addFilters = false) // Desactiva seguridad para evitar el 401
public class PagoControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @MockBean
    private ConsultarEstadoPagoUseCase consultarEstadoPagoUseCase;

    @Test
    @WithMockUser // Añade un usuario mock por si acaso
    void obtenerEstadoPago_CuandoPagoExiste_DebeRetornar200OK() throws Exception {
        // Arrange
        UUID pagoId = UUID.randomUUID();
        EstadoPagoResponseDTO responseDTO = new EstadoPagoResponseDTO(
                pagoId,
                EstadoPagoEnum.PAGADO.name(),
                LocalDateTime.now(),
                new BigDecimal("1000.00"),
                null,
                UUID.randomUUID()
        );
        when(consultarEstadoPagoUseCase.ejecutar(pagoId)).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/pagos/{id}", pagoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pagoId").value(pagoId.toString()))
                .andExpect(jsonPath("$.estado").value("PAGADO"));
    }

    @Test
    @WithMockUser // Añade un usuario mock por si acaso
    void obtenerEstadoPago_CuandoPagoNoExiste_DebeRetornar404NotFound() throws Exception {
        // Arrange
        UUID pagoId = UUID.randomUUID();
        when(consultarEstadoPagoUseCase.ejecutar(pagoId)).thenThrow(new PagoNoEncontradoException("Pago no encontrado"));

        // Act & Assert
        mockMvc.perform(get("/api/pagos/{id}", pagoId))
                .andExpect(status().isNotFound());
    }
}
