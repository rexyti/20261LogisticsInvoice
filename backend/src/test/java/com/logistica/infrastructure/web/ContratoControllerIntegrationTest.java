package com.logistica.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistica.application.dtos.request.ContratoRequestDTO;
import com.logistica.application.dtos.response.ContratoResponseDTO;
import com.logistica.domain.enums.TipoContrato;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ContratoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private ContratoRequestDTO dtoValido() {
        return ContratoRequestDTO.builder()
                .idContrato("CONT-TEST-001")
                .tipoContrato(TipoContrato.POR_PARADA)
                .nombreConductor("Carlos López")
                .precioParadas(new BigDecimal("18.00"))
                .tipoVehiculo("CAMION")
                .fechaInicio(LocalDate.of(2026, 1, 1))
                .fechaFinal(LocalDate.of(2026, 12, 31))
                .estadoSeguro("VIGENTE")
                .build();
    }

    @Test
    @DisplayName("T012 - POST válido retorna HTTP 201 con el contrato persistido")
    @WithMockUser(roles = "GESTOR_TARIFAS")
    void debeRegistrarContratoValidoYRetornar201() throws Exception {
        String body = objectMapper.writeValueAsString(dtoValido());

        MvcResult result = mockMvc.perform(post("/api/contratos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id_contrato").value("CONT-TEST-001"))
                .andExpect(jsonPath("$.nombre_conductor").value("Carlos López"))
                .andExpect(jsonPath("$.tipo_contrato").value("POR_PARADA"))
                .andExpect(jsonPath("$.estado_seguro").value("VIGENTE"))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        ContratoResponseDTO response = objectMapper.readValue(responseBody, ContratoResponseDTO.class);
        assertThat(response.getId()).isNotNull();
    }

    @Test
    @DisplayName("T010 - Campos obligatorios faltantes retornan HTTP 400 con mapa de errores")
    @WithMockUser(roles = "GESTOR_TARIFAS")
    void debeFallarConCamposObligatoriosFaltantes() throws Exception {
        ContratoRequestDTO dtoIncompleto = ContratoRequestDTO.builder()
                .idContrato("CONT-INCOMPLETO")
                .build();

        mockMvc.perform(post("/api/contratos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoIncompleto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").exists())
                .andExpect(jsonPath("$.errores").isMap());
    }

    @Test
    @DisplayName("T010 - Fechas inválidas retornan HTTP 400 con error en fechaFinal")
    @WithMockUser(roles = "GESTOR_TARIFAS")
    void debeFallarCuandoFechaFinalEsAnteriorAFechaInicio() throws Exception {
        ContratoRequestDTO dto = dtoValido();
        dto.setIdContrato("CONT-TEST-FECHAS");
        dto.setFechaInicio(LocalDate.of(2026, 6, 1));
        dto.setFechaFinal(LocalDate.of(2026, 1, 1));

        mockMvc.perform(post("/api/contratos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.fechaFinal").exists());
    }

    @Test
    @DisplayName("T011 - Registro duplicado retorna HTTP 409 con mensaje específico")
    @WithMockUser(roles = "GESTOR_TARIFAS")
    void debeRetornar409CuandoContratoYaExiste() throws Exception {
        String body = objectMapper.writeValueAsString(dtoValido());

        mockMvc.perform(post("/api/contratos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/contratos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje",
                        containsString("El contrato con este identificador ya existe")));
    }

    @Test
    @DisplayName("T018 - GET contrato existente retorna todos los campos del spec")
    @WithMockUser(roles = "GESTOR_TARIFAS")
    void debeRetornarTodosLosCamposDelContrato() throws Exception {
        mockMvc.perform(post("/api/contratos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoValido())))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/contratos/CONT-TEST-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id_contrato").value("CONT-TEST-001"))
                .andExpect(jsonPath("$.tipo_contrato").value("POR_PARADA"))
                .andExpect(jsonPath("$.nombre_conductor").value("Carlos López"))
                .andExpect(jsonPath("$.precio_paradas").value(18.00))
                .andExpect(jsonPath("$.tipo_vehiculo").value("CAMION"))
                .andExpect(jsonPath("$.fecha_inicio").value("2026-01-01"))
                .andExpect(jsonPath("$.fecha_final").value("2026-12-31"))
                .andExpect(jsonPath("$.estado_seguro").value("VIGENTE"));
    }

    @Test
    @DisplayName("T019 - GET contrato inexistente retorna HTTP 404 con mensaje")
    @WithMockUser(roles = "GESTOR_TARIFAS")
    void debeRetornar404CuandoContratoNoExiste() throws Exception {
        mockMvc.perform(get("/api/contratos/CONT-INEXISTENTE"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Contrato no encontrado"));
    }

    @Test
    @DisplayName("Sin autenticación retorna HTTP 401")
    void debeRetornar401SinAutenticacion() throws Exception {
        mockMvc.perform(post("/api/contratos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoValido())))
                .andExpect(status().isUnauthorized());
    }
}
