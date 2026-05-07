package com.logistica.VisualizarLiquidación.infrastructure.web.controllers;

import com.logistica.application.visualizarLiquidacion.dtos.request.VisualizarLiquidacionFiltroDTO;
import com.logistica.application.visualizarLiquidacion.dtos.response.VisualizarLiquidacionDetalleDTO;
import com.logistica.application.visualizarLiquidacion.dtos.response.VisualizarLiquidacionListItemDTO;
import com.logistica.application.visualizarLiquidacion.dtos.response.VisualizarLiquidacionListResponseDTO;
import com.logistica.application.visualizarLiquidacion.usecases.liquidacion.VisualizarLiquidacionBuscarUseCase;
import com.logistica.application.visualizarLiquidacion.usecases.liquidacion.VisualizarLiquidacionListarUseCase;
import com.logistica.application.visualizarLiquidacion.usecases.liquidacion.VisualizarLiquidacionObtenerDetalleUseCase;
import com.logistica.infrastructure.shared.security.JwtAuthenticationFilter;
import com.logistica.infrastructure.visualizarLiquidacion.web.controllers.VisualizarLiquidacionController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VisualizarLiquidacionController.class)
@AutoConfigureMockMvc(addFilters = false)
class LiquidacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private VisualizarLiquidacionListarUseCase listarUseCase;

    @MockBean
    private VisualizarLiquidacionObtenerDetalleUseCase obtenerDetalleUseCase;

    @MockBean
    private VisualizarLiquidacionBuscarUseCase buscarUseCase;

    @Test
    void listar_ok_contenido() throws Exception {
        VisualizarLiquidacionListItemDTO item = new VisualizarLiquidacionListItemDTO(
                UUID.randomUUID(), UUID.randomUUID(),
                LocalDateTime.now(), LocalDateTime.now(),
                "CAMION", BigDecimal.valueOf(1000), 5,
                BigDecimal.valueOf(5000), BigDecimal.valueOf(4500),
                "CALCULADA", LocalDateTime.now(), List.of()
        );

        VisualizarLiquidacionListResponseDTO response = new VisualizarLiquidacionListResponseDTO(
                List.of(item), 0, 10, 1, 1, true
        );

        when(listarUseCase.ejecutar(any(Pageable.class), any())).thenReturn(response);

        mockMvc.perform(get("/api/liquidaciones")
                        .with(autenticado("admin", "ROLE_ADMIN"))
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "fechaCalculo")
                        .param("sortDir", "desc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contenido").isArray())
                .andExpect(jsonPath("$.contenido[0].tipo_vehiculo").value("CAMION"));
    }

    @Test
    void obtenerDetalle_ok() throws Exception {
        UUID id = UUID.randomUUID();
        VisualizarLiquidacionDetalleDTO response = new VisualizarLiquidacionDetalleDTO(
                id, UUID.randomUUID(), UUID.randomUUID(),
                LocalDateTime.now(), LocalDateTime.now(),
                "CAMION", BigDecimal.valueOf(1000), 5,
                BigDecimal.valueOf(5000), BigDecimal.valueOf(4500),
                "CALCULADA", LocalDateTime.now(), "user-id", List.of()
        );

        when(obtenerDetalleUseCase.ejecutar(eq(id), any())).thenReturn(response);

        mockMvc.perform(get("/api/liquidaciones/{id}", id)
                        .with(autenticado("user-id", "ROLE_TRANSPORTISTA"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id_liquidacion").value(id.toString()))
                .andExpect(jsonPath("$.estado_liquidacion").value("CALCULADA"));
    }

    @Test
    void buscar_ok() throws Exception {
        UUID id = UUID.randomUUID();
        VisualizarLiquidacionDetalleDTO response = new VisualizarLiquidacionDetalleDTO(
                id, UUID.randomUUID(), UUID.randomUUID(),
                LocalDateTime.now(), LocalDateTime.now(),
                "CAMION", BigDecimal.valueOf(1000), 5,
                BigDecimal.valueOf(5000), BigDecimal.valueOf(4500),
                "CALCULADA", LocalDateTime.now(), "user-id", List.of()
        );

        when(buscarUseCase.ejecutar(any(VisualizarLiquidacionFiltroDTO.class), any())).thenReturn(response);

        mockMvc.perform(get("/api/liquidaciones/buscar")
                        .with(autenticado("user-id", "ROLE_GESTOR_FINANCIERO"))
                        .param("idRuta", UUID.randomUUID().toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado_liquidacion").value("CALCULADA"));
    }

    @Test
    void buscar_sinCriterios_retorna400() throws Exception {
        mockMvc.perform(get("/api/liquidaciones/buscar")
                        .with(autenticado("user-id", "ROLE_GESTOR_FINANCIERO"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("VALIDACION_FALLIDA"));
    }

    private static RequestPostProcessor autenticado(String userId, String... roles) {
        return request -> {
            var authorities = Arrays.stream(roles)
                    .map(SimpleGrantedAuthority::new)
                    .toList();
            var auth = new UsernamePasswordAuthenticationToken(userId, null, authorities);
            SecurityContext ctx = SecurityContextHolder.createEmptyContext();
            ctx.setAuthentication(auth);
            SecurityContextHolder.setContext(ctx);
            request.setUserPrincipal(auth);
            return request;
        };
    }
}
