package com.logistica.infrastructure.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistica.application.liquidacion.dtos.request.AjusteDTO;
import com.logistica.application.liquidacion.dtos.request.RecalcularRequestDTO;
import com.logistica.application.liquidacion.dtos.response.LiquidacionResponseDTO;
import com.logistica.application.liquidacion.usecases.LiquidacionRecalcularUseCase;
import com.logistica.domain.liquidacion.enums.EstadoLiquidacion;
import com.logistica.domain.liquidacion.enums.TipoAjuste;
import com.logistica.domain.liquidacion.models.Liquidacion;
import com.logistica.infrastructure.liquidacion.persistence.mapper.AjusteMapper;
import com.logistica.infrastructure.liquidacion.persistence.mapper.Mapper;
import com.logistica.infrastructure.liquidacion.web.controllers.LiquidacionRecalcularController;
import com.logistica.infrastructure.shared.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LiquidacionRecalcularController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("LiquidacionRecalcularController - Tests")
class LiquidacionControllerTest {

    private static final UUID LIQUIDACION_ID = UUID.randomUUID();
    private static final String ENDPOINT = "/api/liquidaciones/{id}/recalcular";
    private static final BigDecimal VALOR_RECALCULADO = new BigDecimal("1250.00");

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private LiquidacionRecalcularUseCase recalcularLiquidacionUseCase;
    @MockBean private Mapper liquidacionMapper;
    @MockBean private AjusteMapper ajusteMapper;
    @MockBean private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Nested
    @DisplayName("Recalcular Liquidación")
    class RecalculateOperation {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Debe retornar 200 OK cuando el request es válido")
        void shouldReturn200WhenRecalculateIsValid() throws Exception {
            RecalcularRequestDTO request = createValidRequest();

            Liquidacion mockLiq = Liquidacion.builder()
                    .id(LIQUIDACION_ID)
                    .estado(EstadoLiquidacion.RECALCULADA)
                    .build();

            LiquidacionResponseDTO response = LiquidacionResponseDTO.builder()
                    .id(LIQUIDACION_ID)
                    .estado(EstadoLiquidacion.RECALCULADA)
                    .valorFinal(VALOR_RECALCULADO)
                    .build();

            when(ajusteMapper.toModelList(anyList())).thenReturn(List.of());
            when(recalcularLiquidacionUseCase.execute(eq(LIQUIDACION_ID), anyList(), anyString()))
                    .thenReturn(mockLiq);
            when(liquidacionMapper.toResponseDTO(any())).thenReturn(response);

            mockMvc.perform(put(ENDPOINT, LIQUIDACION_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(LIQUIDACION_ID.toString()))
                    .andExpect(jsonPath("$.estado").value(EstadoLiquidacion.RECALCULADA.name()))
                    .andExpect(jsonPath("$.valor_final").value(VALOR_RECALCULADO.doubleValue()));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Debe retornar 400 cuando faltan ajustes")
        void shouldReturn400WhenAjustesMissing() throws Exception {
            RecalcularRequestDTO invalidRequest = new RecalcularRequestDTO();
            invalidRequest.setResponsable("ADMIN");

            mockMvc.perform(put(ENDPOINT, LIQUIDACION_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Debe retornar 403 cuando el usuario no es ADMIN")
        void shouldReturn403WhenUserIsNotAdmin() throws Exception {
            when(ajusteMapper.toModelList(anyList())).thenReturn(List.of());
            when(recalcularLiquidacionUseCase.execute(eq(LIQUIDACION_ID), anyList(), anyString()))
                    .thenThrow(new AccessDeniedException("No tiene permisos"));

            mockMvc.perform(put(ENDPOINT, LIQUIDACION_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createValidRequest())))
                    .andExpect(status().isForbidden());
        }
    }

    private RecalcularRequestDTO createValidRequest() {
        AjusteDTO ajuste = new AjusteDTO();
        ajuste.setTipo(TipoAjuste.BONO);
        ajuste.setMonto(new BigDecimal("50.00"));
        ajuste.setMotivo("Corrección manual");

        RecalcularRequestDTO request = new RecalcularRequestDTO();
        request.setAjustes(List.of(ajuste));
        request.setResponsable("ADMIN_USER");
        return request;
    }
}
