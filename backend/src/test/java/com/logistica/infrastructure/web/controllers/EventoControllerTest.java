package com.logistica.infrastructure.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistica.liquidacion.application.dtos.request.CierreRutaEventDTO;
import com.logistica.liquidacion.application.dtos.request.PaqueteDTO;
import com.logistica.liquidacion.application.dtos.response.LiquidacionResponseDTO;
import com.logistica.liquidacion.application.usecases.CalcularLiquidacionUseCase;
import com.logistica.liquidacion.domain.enums.EstadoLiquidacion;
import com.logistica.liquidacion.domain.enums.EstadoPaquete;
import com.logistica.liquidacion.domain.models.Liquidacion;
import com.logistica.liquidacion.domain.models.Ruta;
import com.logistica.liquidacion.infrastructure.config.JwtAuthenticationFilter;
import com.logistica.liquidacion.infrastructure.config.JwtService;
import com.logistica.liquidacion.infrastructure.persistence.mapper.LiquidacionMapper;
import com.logistica.liquidacion.infrastructure.persistence.mapper.RutaMapper;
import com.logistica.liquidacion.infrastructure.web.controllers.EventoController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventoController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(LocalValidatorFactoryBean.class)
@DisplayName("EventoController - Tests")
class EventoControllerTest {

    // =========================
    // Constantes
    // =========================
    private static final UUID RUTA_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID CONTRATO_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID LIQ_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private static final BigDecimal VALOR_FINAL = new BigDecimal("150.0");
    private static final String ENDPOINT = "/api/eventos/cierre-ruta";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private CalcularLiquidacionUseCase calcularLiquidacionUseCase;
    @MockBean private LiquidacionMapper liquidacionMapper;
    @MockBean private RutaMapper rutaMapper;

    // Seguridad (mock para evitar errores de contexto)
    @MockBean private JwtService jwtService;
    @MockBean private JwtAuthenticationFilter jwtAuthenticationFilter;

    private CierreRutaEventDTO validEventDTO;
    private Ruta ruta;
    private Liquidacion liquidacion;
    private LiquidacionResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        validEventDTO = buildEventDTO();
        ruta = buildRutaValida();
        liquidacion = buildLiquidacion();
        responseDTO = buildResponseDTO();
    }

    // =========================================================================
    // Happy path
    // =========================================================================
    @Nested
    @DisplayName("Flujo exitoso")
    class FlujoExitoso {

        @BeforeEach
        void stubs() {
            when(rutaMapper.toModel(any())).thenReturn(ruta);
            when(calcularLiquidacionUseCase.execute(eq(ruta), eq(CONTRATO_ID)))
                    .thenReturn(liquidacion);
            when(liquidacionMapper.toResponseDTO(any()))
                    .thenReturn(responseDTO);
        }

        @Test
        @DisplayName("Debe retornar 200 con la liquidación")
        void deberia_retornar200() throws Exception {
            ejecutarPost(validEventDTO)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(LIQ_ID.toString()))
                    .andExpect(jsonPath("$.idRuta").value(RUTA_ID.toString()))
                    .andExpect(jsonPath("$.estado").value(EstadoLiquidacion.CALCULADA.name()))
                    .andExpect(jsonPath("$.valorFinal").value(VALOR_FINAL));
        }

        @Test
        void deberia_invocar_useCase() throws Exception {
            ejecutarPost(validEventDTO);

            verify(calcularLiquidacionUseCase)
                    .execute(eq(ruta), eq(CONTRATO_ID));
        }

        @Test
        void deberia_usar_mapper() throws Exception {
            ejecutarPost(validEventDTO);

            verify(rutaMapper).toModel(any());
            verify(liquidacionMapper).toResponseDTO(any());
        }
    }

    // =========================================================================
    // Validaciones
    // =========================================================================
    @Nested
    @DisplayName("Validaciones")
    class Validaciones {

        @Test
        void deberia_retornar400_paquetes_vacio() throws Exception {
            validEventDTO.setPaquetes(List.of());

            ejecutarPost(validEventDTO)
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(calcularLiquidacionUseCase);
        }

        @Test
        void deberia_retornar400_paquetes_null() throws Exception {
            validEventDTO.setPaquetes(null);

            ejecutarPost(validEventDTO)
                    .andExpect(status().isBadRequest());
        }

        @Test
        void deberia_retornar400_idContrato_null() throws Exception {
            validEventDTO.setIdContrato(null);

            ejecutarPost(validEventDTO)
                    .andExpect(status().isBadRequest());
        }
    }

    // =========================================================================
    // Errores internos
    // =========================================================================
    @Nested
    @DisplayName("Errores internos")
    class Errores {

        @Test
        void deberia_retornar500() throws Exception {
            when(rutaMapper.toModel(any())).thenReturn(ruta);
            when(calcularLiquidacionUseCase.execute(any(), any()))
                    .thenThrow(new RuntimeException());

            ejecutarPost(validEventDTO)
                    .andExpect(status().isInternalServerError());
        }
    }

    // =========================================================================
    // Helpers
    // =========================================================================
    private org.springframework.test.web.servlet.ResultActions ejecutarPost(CierreRutaEventDTO dto) throws Exception {
        return mockMvc.perform(post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));
    }

    // =========================================================================
    // Factories
    // =========================================================================
    private static CierreRutaEventDTO buildEventDTO() {
        PaqueteDTO paquete = new PaqueteDTO();
        paquete.setId(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"));
        paquete.setEstadoFinal(EstadoPaquete.ENTREGADO);

        CierreRutaEventDTO dto = new CierreRutaEventDTO();
        dto.setIdRuta(RUTA_ID);
        dto.setIdContrato(CONTRATO_ID);
        dto.setFechaInicio(OffsetDateTime.now().minusHours(2));
        dto.setFechaCierre(OffsetDateTime.now());
        dto.setPaquetes(List.of(paquete));

        return dto;
    }

    private static Ruta buildRutaValida() {
        return Ruta.builder()
                .id(RUTA_ID)
                .fechaInicio(OffsetDateTime.now().minusHours(2))
                .fechaCierre(OffsetDateTime.now())
                .paquetes(List.of())
                .build();
    }

    private static Liquidacion buildLiquidacion() {
        return Liquidacion.builder()
                .id(LIQ_ID)
                .idRuta(RUTA_ID)
                .idContrato(CONTRATO_ID)
                .estado(EstadoLiquidacion.CALCULADA)
                .valorFinal(VALOR_FINAL)
                .build();
    }

    private static LiquidacionResponseDTO buildResponseDTO() {
        return LiquidacionResponseDTO.builder()
                .id(LIQ_ID)
                .idRuta(RUTA_ID)
                .estado(EstadoLiquidacion.CALCULADA)
                .valorFinal(VALOR_FINAL)
                .build();
    }
}