package com.logistica.VisualizarEstadoPago.integration;

import com.logistica.VisualizarEstadoPago.domain.enums.EstadoPagoEnum;
import com.logistica.VisualizarEstadoPago.infrastructure.persistence.entities.PagoEntity;
import com.logistica.VisualizarEstadoPago.infrastructure.persistence.repositories.PagoJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class VisualizarEstadoPagoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PagoJpaRepository pagoJpaRepository;

    private static final String USUARIO_UUID = "11111111-1111-1111-1111-111111111111";
    private static final UUID USUARIO_ID = UUID.fromString(USUARIO_UUID);
    private static final String OTRO_USUARIO_UUID = "22222222-2222-2222-2222-222222222222";

    @BeforeEach
    void setUp() {
        pagoJpaRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = USUARIO_UUID)
    void obtenerEstadoPago_CuandoPagoEsDelUsuario_Retorna200() throws Exception {
        PagoEntity saved = pagoJpaRepository.saveAndFlush(pagoConEstado(USUARIO_ID, EstadoPagoEnum.PAGADO));

        mockMvc.perform(get("/api/pagos/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pagoId").value(saved.getId().toString()))
                .andExpect(jsonPath("$.estado").value("PAGADO"))
                .andExpect(jsonPath("$.liquidacionId").exists());
    }

    @Test
    @WithMockUser(username = USUARIO_UUID)
    void obtenerEstadoPago_CuandoEstadoPendiente_MuestraEstadoCorrecto() throws Exception {
        PagoEntity saved = pagoJpaRepository.saveAndFlush(pagoConEstado(USUARIO_ID, EstadoPagoEnum.PENDIENTE));

        mockMvc.perform(get("/api/pagos/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    @WithMockUser(username = USUARIO_UUID)
    void obtenerEstadoPago_CuandoEstadoRechazado_MuestraEstadoCorrecto() throws Exception {
        PagoEntity saved = pagoJpaRepository.saveAndFlush(pagoConEstado(USUARIO_ID, EstadoPagoEnum.RECHAZADO));

        mockMvc.perform(get("/api/pagos/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("RECHAZADO"));
    }

    @Test
    @WithMockUser(username = USUARIO_UUID)
    void obtenerEstadoPago_CuandoPagoNoExiste_Retorna404() throws Exception {
        mockMvc.perform(get("/api/pagos/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @WithMockUser(username = OTRO_USUARIO_UUID)
    void obtenerEstadoPago_CuandoUsuarioNoEsPropietario_Retorna403() throws Exception {
        PagoEntity saved = pagoJpaRepository.saveAndFlush(pagoConEstado(USUARIO_ID, EstadoPagoEnum.PAGADO));

        mockMvc.perform(get("/api/pagos/{id}", saved.getId()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @WithMockUser(username = USUARIO_UUID)
    void listarPagos_RetornaUnicamentePagosDelUsuarioAutenticado() throws Exception {
        pagoJpaRepository.saveAndFlush(pagoConEstado(USUARIO_ID, EstadoPagoEnum.PAGADO));
        pagoJpaRepository.saveAndFlush(pagoConEstado(USUARIO_ID, EstadoPagoEnum.PENDIENTE));
        pagoJpaRepository.saveAndFlush(pagoConEstado(UUID.fromString(OTRO_USUARIO_UUID), EstadoPagoEnum.PAGADO));

        mockMvc.perform(get("/api/pagos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    private PagoEntity pagoConEstado(UUID usuarioId, EstadoPagoEnum estado) {
        PagoEntity entity = new PagoEntity();
        entity.setUsuarioId(usuarioId);
        entity.setMontoBase(new BigDecimal("1000.00"));
        entity.setMontoNeto(new BigDecimal("900.00"));
        entity.setFecha(LocalDateTime.now());
        entity.setLiquidacionId(UUID.randomUUID());
        entity.setEstado(estado);
        return entity;
    }
}
