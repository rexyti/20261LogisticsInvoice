package com.logistica.VisualizarEstadoPago.integration;

import com.logistica.domain.registrarEstadoPago.enums.RegistrarEstadoPagoEstadoPagoEnum;
import com.logistica.infrastructure.registrarEstadoPago.persistence.entities.RegistrarEstadoPagoPagoEntity;
import com.logistica.infrastructure.visualizarEstadoPago.persistence.repositories.VisualizarEstadoPagoPagoJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
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
    private VisualizarEstadoPagoPagoJpaRepository pagoJpaRepository;

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
        RegistrarEstadoPagoPagoEntity saved = pagoJpaRepository.saveAndFlush(
                pagoConEstado(USUARIO_ID, RegistrarEstadoPagoEstadoPagoEnum.PAGADO));

        mockMvc.perform(get("/api/pagos/{id}", saved.getIdPago()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pago_id").value(saved.getIdPago().toString()))
                .andExpect(jsonPath("$.estado").value("PAGADO"))
                .andExpect(jsonPath("$.liquidacion_id").exists());
    }

    @Test
    @WithMockUser(username = USUARIO_UUID)
    void obtenerEstadoPago_CuandoEstadoPendiente_MuestraEstadoCorrecto() throws Exception {
        RegistrarEstadoPagoPagoEntity saved = pagoJpaRepository.saveAndFlush(
                pagoConEstado(USUARIO_ID, RegistrarEstadoPagoEstadoPagoEnum.PENDIENTE));

        mockMvc.perform(get("/api/pagos/{id}", saved.getIdPago()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    @WithMockUser(username = USUARIO_UUID)
    void obtenerEstadoPago_CuandoEstadoRechazado_MuestraEstadoCorrecto() throws Exception {
        RegistrarEstadoPagoPagoEntity saved = pagoJpaRepository.saveAndFlush(
                pagoConEstado(USUARIO_ID, RegistrarEstadoPagoEstadoPagoEnum.RECHAZADO));

        mockMvc.perform(get("/api/pagos/{id}", saved.getIdPago()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("RECHAZADO"));
    }

    @Test
    @WithMockUser(username = USUARIO_UUID)
    void obtenerEstadoPago_CuandoPagoNoExiste_Retorna404() throws Exception {
        mockMvc.perform(get("/api/pagos/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").exists());
    }

    @Test
    @WithMockUser(username = OTRO_USUARIO_UUID)
    void obtenerEstadoPago_CuandoUsuarioNoEsPropietario_Retorna403() throws Exception {
        RegistrarEstadoPagoPagoEntity saved = pagoJpaRepository.saveAndFlush(
                pagoConEstado(USUARIO_ID, RegistrarEstadoPagoEstadoPagoEnum.PAGADO));

        mockMvc.perform(get("/api/pagos/{id}", saved.getIdPago()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.codigo").exists());
    }

    @Test
    @WithMockUser(username = USUARIO_UUID)
    void listarPagos_RetornaUnicamentePagosDelUsuarioAutenticado() throws Exception {
        pagoJpaRepository.saveAndFlush(pagoConEstado(USUARIO_ID, RegistrarEstadoPagoEstadoPagoEnum.PAGADO));
        pagoJpaRepository.saveAndFlush(pagoConEstado(USUARIO_ID, RegistrarEstadoPagoEstadoPagoEnum.PENDIENTE));
        pagoJpaRepository.saveAndFlush(pagoConEstado(UUID.fromString(OTRO_USUARIO_UUID), RegistrarEstadoPagoEstadoPagoEnum.PAGADO));

        mockMvc.perform(get("/api/pagos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    private RegistrarEstadoPagoPagoEntity pagoConEstado(UUID usuarioId, RegistrarEstadoPagoEstadoPagoEnum estado) {
        return RegistrarEstadoPagoPagoEntity.builder()
                .idPago(UUID.randomUUID())
                .idUsuario(usuarioId)
                .montoBase(new BigDecimal("1000.00"))
                .montoNeto(new BigDecimal("900.00"))
                .fecha(Instant.now())
                .idLiquidacion(UUID.randomUUID())
                .estadoActual(estado)
                .fechaUltimaActualizacion(Instant.now())
                .ultimaSecuenciaProcesada(1L)
                .build();
    }
}
