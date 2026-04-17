package com.logistica.infrastructure.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistica.application.dtos.request.CierreRutaEventDTO;
import com.logistica.application.dtos.request.PaqueteDTO;
import com.logistica.application.dtos.response.LiquidacionResponseDTO;
import com.logistica.application.usecases.CalcularLiquidacionUseCase;
import com.logistica.domain.enums.EstadoLiquidacion;
import com.logistica.domain.enums.EstadoPaquete;
import com.logistica.domain.models.Liquidacion;
import com.logistica.domain.models.Ruta;
import com.logistica.infrastructure.persistence.mapper.LiquidacionMapper;
import com.logistica.infrastructure.persistence.mapper.RutaMapper;
import org.junit.jupiter.api.BeforeEach;
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
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventoController.class)
@DisplayName("EventoController - Tests")
class EventoControllerTest {

    private static final UUID RUTA_ID     = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID CONTRATO_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID LIQ_ID      = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private static final BigDecimal VALOR_FINAL = new BigDecimal("100.00");
    private static final String ENDPOINT = "/api/eventos/cierre-ruta";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private CalcularLiquidacionUseCase calcularLiquidacionUseCase;
    @MockBean private LiquidacionMapper liquidacionMapper;
    @MockBean private RutaMapper rutaMapper;

    private CierreRutaEventDTO eventDTO;
    private Ruta ruta;
    private Liquidacion liquidacion;
    private LiquidacionResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        PaqueteDTO paqueteDTO = new PaqueteDTO();
        paqueteDTO.setId(UUID.randomUUID());
        paqueteDTO.setEstadoFinal(EstadoPaquete.ENTREGADO);

        eventDTO = new CierreRutaEventDTO();
        eventDTO.setIdRuta(RUTA_ID);
        eventDTO.setIdContrato(CONTRATO_ID);
        eventDTO.setFechaInicio(OffsetDateTime.now().minusHours(5));
        eventDTO.setFechaCierre(OffsetDateTime.now());
        eventDTO.setPaquetes(List.of(paqueteDTO));

        ruta = Ruta.builder().id(RUTA_ID).build();
        
        liquidacion = Liquidacion.builder()
                .id(LIQ_ID)
                .idRuta(RUTA_ID)
                .idContrato(CONTRATO_ID)
                .estado(EstadoLiquidacion.CALCULADA)
                .valorBase(VALOR_FINAL)
                .valorFinal(VALOR_FINAL)
                .fechaCalculo(OffsetDateTime.now())
                .build();

        responseDTO = LiquidacionResponseDTO.builder()
                .id(LIQ_ID)
                .idRuta(RUTA_ID)
                .estado(EstadoLiquidacion.CALCULADA)
                .valorFinal(VALOR_FINAL)
                .build();
    }

    @Nested
    @DisplayName("POST /api/eventos/cierre-ruta - flujo exitoso")
    class FlujoExitoso {

        @BeforeEach
        void stubearColaboradores() {
            when(rutaMapper.toModel(any(CierreRutaEventDTO.class))).thenReturn(ruta);
            when(calcularLiquidacionUseCase.execute(any(Ruta.class), eq(CONTRATO_ID))).thenReturn(liquidacion);
            when(liquidacionMapper.toResponseDTO(any(Liquidacion.class))).thenReturn(responseDTO);
        }

        @Test
        @WithMockUser
        @DisplayName("Debe retornar 200 con los datos de la liquidación calculada")
        void deberia_retornar200_con_liquidacion_calculada() throws Exception {
            mockMvc.perform(post(ENDPOINT)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(eventDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(LIQ_ID.toString()))
                    .andExpect(jsonPath("$.idRuta").value(RUTA_ID.toString()));
        }
    }

    @Nested
    @DisplayName("POST /api/eventos/cierre-ruta - validaciones")
    class ValidacionesDeRequest {

        @Test
        @WithMockUser
        @DisplayName("Debe retornar 400 cuando paquetes está vacío")
        void deberia_retornar400_cuando_paquetes_esta_vacio() throws Exception {
            eventDTO.setPaquetes(List.of());

            mockMvc.perform(post(ENDPOINT)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(eventDTO)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(calcularLiquidacionUseCase);
        }

        @Test
        @WithMockUser
        @DisplayName("Debe retornar 415 cuando el Content-Type no es JSON")
        void deberia_retornar415_cuando_contentType_no_es_json() throws Exception {
            mockMvc.perform(post(ENDPOINT).with(csrf())
                            .contentType(MediaType.TEXT_PLAIN)
                            .content("texto plano"))
                    .andExpect(status().isUnsupportedMediaType());

            verifyNoInteractions(calcularLiquidacionUseCase);
        }
    }

}
