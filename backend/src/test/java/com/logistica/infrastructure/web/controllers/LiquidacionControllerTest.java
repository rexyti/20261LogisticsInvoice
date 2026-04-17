package com.logistica.infrastructure.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistica.application.dtos.request.AjusteDTO;
import com.logistica.application.dtos.request.RecalcularLiquidacionRequestDTO;
import com.logistica.application.dtos.response.LiquidacionResponseDTO;
import com.logistica.application.usecases.RecalcularLiquidacionUseCase;
import com.logistica.domain.enums.EstadoLiquidacion;
import com.logistica.domain.enums.TipoAjuste;
import com.logistica.domain.models.Liquidacion;
import com.logistica.infrastructure.persistence.mapper.AjusteMapper;
import com.logistica.infrastructure.persistence.mapper.LiquidacionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LiquidacionController.class)
@DisplayName("LiquidacionController - Tests")
class LiquidacionControllerTest {

    // =========================
    // Constantes deterministas
    // =========================
    private static final UUID LIQ_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final String ENDPOINT = "/api/liquidaciones/{id}/recalcular";
    private static final BigDecimal VALOR_FINAL = new BigDecimal("120.00");

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private RecalcularLiquidacionUseCase recalcularLiquidacionUseCase;
    @MockBean private LiquidacionMapper liquidacionMapper;
    @MockBean private AjusteMapper ajusteMapper;

    private RecalcularLiquidacionRequestDTO requestDTO;
    private Liquidacion liquidacion;
    private LiquidacionResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = buildRequestDTO();
        liquidacion = buildLiquidacion();
        responseDTO = buildResponseDTO();
    }

    // =========================================================================
    // Happy path
    // =========================================================================
    @Nested
    @DisplayName("PUT /api/liquidaciones/{id}/recalcular - flujo exitoso")
    class FlujoExitoso {

        @BeforeEach
        void stubs() {
            when(ajusteMapper.toModelList(anyList())).thenReturn(List.of());
            when(recalcularLiquidacionUseCase.execute(eq(LIQ_ID), anyList(), anyString()))
                    .thenReturn(liquidacion);
            when(liquidacionMapper.toResponseDTO(any(Liquidacion.class)))
                    .thenReturn(responseDTO);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Debe retornar 200 con la liquidación recalculada")
        void deberia_retornar200() throws Exception {
            ejecutarPut(requestDTO)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(LIQ_ID.toString()))
                    .andExpect(jsonPath("$.estado").value(EstadoLiquidacion.RECALCULADA.name()))
                    .andExpect(jsonPath("$.valorFinal").value(VALOR_FINAL));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Debe delegar el mapeo de ajustes")
        void deberia_delegar_a_ajusteMapper() throws Exception {
            ejecutarPut(requestDTO);

            verify(ajusteMapper).toModelList(anyList());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Debe invocar el UseCase con parámetros correctos")
        void deberia_invocar_useCase() throws Exception {
            ejecutarPut(requestDTO);

            verify(recalcularLiquidacionUseCase)
                    .execute(eq(LIQ_ID), anyList(), eq("ADMIN_USER"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Debe delegar el mapeo a LiquidacionMapper")
        void deberia_delegar_a_mapper() throws Exception {
            ejecutarPut(requestDTO);

            verify(liquidacionMapper).toResponseDTO(eq(liquidacion));
        }
    }

    // =========================================================================
    // Seguridad
    // =========================================================================
    @Nested
    @DisplayName("Seguridad")
    class Seguridad {

        @Test
        @DisplayName("Debe retornar 401 si no está autenticado")
        void deberia_retornar401() throws Exception {
            ejecutarPut(requestDTO).andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Debe retornar 403 si no es ADMIN")
        void deberia_retornar403() throws Exception {
            ejecutarPut(requestDTO).andExpect(status().isForbidden());
        }
    }

    // =========================================================================
    // Validaciones
    // =========================================================================
    @Nested
    @DisplayName("Validaciones")
    class Validaciones {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Debe retornar 400 si el body está vacío")
        void deberia_retornar400_body_vacio() throws Exception {
            ejecutarPutConBody("{}")
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(recalcularLiquidacionUseCase);
        }
    }

    // =========================================================================
    // Errores internos
    // =========================================================================
    @Nested
    @DisplayName("Errores internos")
    class Errores {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Debe retornar 500 si el UseCase falla")
        void deberia_retornar500() throws Exception {
            when(ajusteMapper.toModelList(anyList())).thenReturn(List.of());
            when(recalcularLiquidacionUseCase.execute(any(), anyList(), anyString()))
                    .thenThrow(new RuntimeException("Error"));

            ejecutarPut(requestDTO)
                    .andExpect(status().isInternalServerError());
        }
    }

    // =========================================================================
    // Helpers
    // =========================================================================
    private org.springframework.test.web.servlet.ResultActions ejecutarPut(RecalcularLiquidacionRequestDTO dto) throws Exception {
        return ejecutarPutConBody(objectMapper.writeValueAsString(dto));
    }

    private org.springframework.test.web.servlet.ResultActions ejecutarPutConBody(String body) throws Exception {
        return mockMvc.perform(put(ENDPOINT, LIQ_ID)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    // =========================================================================
    // Factories
    // =========================================================================
    private static RecalcularLiquidacionRequestDTO buildRequestDTO() {
        AjusteDTO ajuste = new AjusteDTO();
        ajuste.setTipo(TipoAjuste.BONO);
        ajuste.setMonto(new BigDecimal("20.00"));
        ajuste.setMotivo("Buen desempeño");

        RecalcularLiquidacionRequestDTO dto = new RecalcularLiquidacionRequestDTO();
        dto.setAjustes(List.of(ajuste));
        dto.setResponsable("ADMIN_USER");

        return dto;
    }

    private static Liquidacion buildLiquidacion() {
        return Liquidacion.builder()
                .id(LIQ_ID)
                .estado(EstadoLiquidacion.RECALCULADA)
                .valorFinal(VALOR_FINAL)
                .build();
    }

    private static LiquidacionResponseDTO buildResponseDTO() {
        return LiquidacionResponseDTO.builder()
                .id(LIQ_ID)
                .estado(EstadoLiquidacion.RECALCULADA)
                .valorFinal(VALOR_FINAL)
                .build();
    }
}
