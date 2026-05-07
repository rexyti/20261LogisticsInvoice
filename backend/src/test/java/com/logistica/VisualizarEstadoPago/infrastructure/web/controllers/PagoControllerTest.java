package com.logistica.VisualizarEstadoPago.infrastructure.web.controllers;

import com.logistica.application.visualizarEstadoPago.dtos.response.PagoListDTO;
import com.logistica.application.visualizarEstadoPago.dtos.response.VisualizarEstadoPagoEstadoPagoResponseDTO;
import com.logistica.application.visualizarEstadoPago.usecases.pago.ConsultarEstadoPagoUseCase;
import com.logistica.application.visualizarEstadoPago.usecases.pago.ListarPagosUseCase;
import com.logistica.domain.visualizarEstadoPago.enums.VisualizarEstadoPagoEstadoPagoEnum;
import com.logistica.domain.visualizarEstadoPago.exceptions.AccessDeniedPaymentException;
import com.logistica.domain.visualizarEstadoPago.exceptions.VisualizarEstadoPagoPagoNoEncontradoException;
import com.logistica.infrastructure.shared.security.JwtAuthenticationFilter;
import com.logistica.infrastructure.visualizarEstadoPago.web.controllers.VisualizarEstadoPagoPagoController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VisualizarEstadoPagoPagoController.class)
@AutoConfigureMockMvc(addFilters = false)
class PagoControllerTest {

    private static final String USUARIO_UUID = "11111111-1111-1111-1111-111111111111";
    private static final UUID USUARIO_ID = UUID.fromString(USUARIO_UUID);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConsultarEstadoPagoUseCase consultarEstadoPagoUseCase;

    @MockBean
    private ListarPagosUseCase listarPagosUseCase;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void obtenerEstadoPago_CuandoPagoExiste_Retorna200() throws Exception {
        UUID pagoId = UUID.randomUUID();
        VisualizarEstadoPagoEstadoPagoResponseDTO responseDTO = new VisualizarEstadoPagoEstadoPagoResponseDTO(
                pagoId, VisualizarEstadoPagoEstadoPagoEnum.PAGADO.name(), LocalDateTime.now(),
                new BigDecimal("1000.00"), null, UUID.randomUUID());
        when(consultarEstadoPagoUseCase.ejecutar(pagoId, USUARIO_ID)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/pagos/{id}", pagoId)
                        .with(usuarioAutenticado(USUARIO_UUID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pago_id").value(pagoId.toString()))
                .andExpect(jsonPath("$.estado").value("PAGADO"));
    }

    @Test
    void obtenerEstadoPago_CuandoPagoNoExiste_Retorna404() throws Exception {
        UUID pagoId = UUID.randomUUID();
        when(consultarEstadoPagoUseCase.ejecutar(pagoId, USUARIO_ID))
                .thenThrow(new VisualizarEstadoPagoPagoNoEncontradoException("VisualizarEstadoPagoPago no encontrado"));

        mockMvc.perform(get("/api/pagos/{id}", pagoId)
                        .with(usuarioAutenticado(USUARIO_UUID)))
                .andExpect(status().isNotFound());
    }

    @Test
    void obtenerEstadoPago_CuandoUsuarioNoTienePermiso_Retorna403() throws Exception {
        UUID pagoId = UUID.randomUUID();
        when(consultarEstadoPagoUseCase.ejecutar(pagoId, USUARIO_ID))
                .thenThrow(new AccessDeniedPaymentException("Acceso denegado"));

        mockMvc.perform(get("/api/pagos/{id}", pagoId)
                        .with(usuarioAutenticado(USUARIO_UUID)))
                .andExpect(status().isForbidden());
    }

    @Test
    void listarPagos_CuandoUsuarioTienePagos_Retorna200ConLista() throws Exception {
        PagoListDTO dto = new PagoListDTO(UUID.randomUUID(), UUID.randomUUID(),
                LocalDateTime.now(), new BigDecimal("500.00"), VisualizarEstadoPagoEstadoPagoEnum.PENDIENTE.name());
        when(listarPagosUseCase.ejecutar(USUARIO_ID)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/pagos")
                        .with(usuarioAutenticado(USUARIO_UUID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));
    }

    private static RequestPostProcessor usuarioAutenticado(String userId) {
        return request -> {
            var auth = new UsernamePasswordAuthenticationToken(userId, null, List.of());
            SecurityContext ctx = SecurityContextHolder.createEmptyContext();
            ctx.setAuthentication(auth);
            SecurityContextHolder.setContext(ctx);
            request.setUserPrincipal(auth);
            return request;
        };
    }
}
