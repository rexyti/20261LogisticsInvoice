package com.logistica.contratos.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistica.contratos.application.dtos.request.ContratoRequestDTO;
import com.logistica.contratos.application.dtos.request.SeguroRequestDTO;
import com.logistica.contratos.domain.enums.TipoVehiculo;
import com.logistica.contratos.infrastructure.persistence.entities.TransportistaEntity;
import com.logistica.contratos.infrastructure.persistence.repositories.TransportistaJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

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

    @Autowired
    private TransportistaJpaRepository transportistaJpaRepository;

    private UUID transportistaId;

    @BeforeEach
    void setUp() {
        TransportistaEntity transportista = TransportistaEntity.builder()
                .nombre("Test Transportista")
                .build();
        transportistaId = transportistaJpaRepository.save(transportista).getIdTransportista();
    }

    private ContratoRequestDTO dtoValido() {
        SeguroRequestDTO seguro = new SeguroRequestDTO();
        seguro.setNumeroPoliza("POL-TEST-001");
        seguro.setEstado("VIGENTE");

        ContratoRequestDTO dto = new ContratoRequestDTO();
        dto.setIdContrato("CONT-TEST-001");
        dto.setTipoContrato("MENSAJERIA");
        dto.setTransportistaId(transportistaId);
        dto.setEsPorParada(true);
        dto.setPrecioParadas(new BigDecimal("18.00"));
        dto.setTipoVehiculo(TipoVehiculo.VAN);
        dto.setFechaInicio(LocalDateTime.of(2026, 1, 1, 0, 0));
        dto.setFechaFinal(LocalDateTime.of(2026, 12, 31, 0, 0));
        dto.setSeguro(seguro);
        return dto;
    }

    @Test
    @DisplayName("T012 - POST válido retorna HTTP 201 con el contrato persistido")
    @WithMockUser(roles = "GESTOR_TARIFAS")
    void debeRegistrarContratoValidoYRetornar201() throws Exception {
        mockMvc.perform(post("/api/contratos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoValido())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id_contrato").value("CONT-TEST-001"))
                .andExpect(jsonPath("$.tipo_contrato").value("MENSAJERIA"))
                .andExpect(jsonPath("$.es_por_parada").value(true))
                .andExpect(jsonPath("$.seguro.estado").value("VIGENTE"));
    }

    @Test
    @DisplayName("T010 - Campos obligatorios faltantes retornan HTTP 400 con mapa de errores")
    @WithMockUser(roles = "GESTOR_TARIFAS")
    void debeFallarConCamposObligatoriosFaltantes() throws Exception {
        ContratoRequestDTO dtoIncompleto = new ContratoRequestDTO();
        dtoIncompleto.setIdContrato("CONT-INCOMPLETO");

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
        dto.setFechaInicio(LocalDateTime.of(2026, 6, 1, 0, 0));
        dto.setFechaFinal(LocalDateTime.of(2026, 1, 1, 0, 0));

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
    @DisplayName("T018 - GET contrato existente retorna los campos principales")
    @WithMockUser(roles = "GESTOR_TARIFAS")
    void debeRetornarTodosLosCamposDelContrato() throws Exception {
        mockMvc.perform(post("/api/contratos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoValido())))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/contratos/CONT-TEST-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id_contrato").value("CONT-TEST-001"))
                .andExpect(jsonPath("$.tipo_contrato").value("MENSAJERIA"))
                .andExpect(jsonPath("$.es_por_parada").value(true))
                .andExpect(jsonPath("$.precio_paradas").value(18.00))
                .andExpect(jsonPath("$.tipo_vehiculo").value("VAN"))
                .andExpect(jsonPath("$.seguro.estado").value("VIGENTE"))
                .andExpect(jsonPath("$.seguro.numero_poliza").value("POL-TEST-001"));
    }

    @Test
    @DisplayName("T019 - GET contrato inexistente retorna HTTP 404 con mensaje")
    @WithMockUser(roles = "GESTOR_TARIFAS")
    void debeRetornar404CuandoContratoNoExiste() throws Exception {
        mockMvc.perform(get("/api/contratos/CONT-INEXISTENTE"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").exists());
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
