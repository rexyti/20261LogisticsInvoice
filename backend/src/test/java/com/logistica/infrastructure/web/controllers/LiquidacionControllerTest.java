package com.logistica.infrastructure.web.controllers;

import com.logistica.application.dtos.request.FiltroLiquidacionDTO;
import com.logistica.application.dtos.response.*;
import com.logistica.application.usecases.liquidacion.*;
import com.logistica.infrastructure.security.JwtAuthenticationFilter;
import com.logistica.infrastructure.security.JwtService;
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

@WebMvcTest(LiquidacionController.class)
@AutoConfigureMockMvc(addFilters = false)
class LiquidacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private ListarLiquidacionesUseCase listarUseCase;

    @MockBean
    private ObtenerDetalleLiquidacionUseCase obtenerDetalleUseCase;

    @MockBean
    private BuscarLiquidacionesUseCase buscarUseCase;

    @Test
    void listar_ok_contenido() throws Exception {
        LiquidacionListItemDTO item = new LiquidacionListItemDTO(
                UUID.randomUUID(),
                UUID.randomUUID(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                "CAMION",
                BigDecimal.valueOf(1000),
                5,
                BigDecimal.valueOf(5000),
                BigDecimal.valueOf(4500),
                "CALCULADA",
                LocalDateTime.now(),
                List.of()

        );

        LiquidacionListResponseDTO response = new LiquidacionListResponseDTO(
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
        LiquidacionDetalleDTO response = new LiquidacionDetalleDTO(
                id,
                UUID.randomUUID(),
                UUID.randomUUID(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                "CAMION",
                BigDecimal.valueOf(1000),
                5,
                BigDecimal.valueOf(5000),
                BigDecimal.valueOf(4500),
                "CALCULADA",
                LocalDateTime.now(),
                "user-id",
                List.of()
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
        LiquidacionDetalleDTO response = new LiquidacionDetalleDTO(
                id,
                UUID.randomUUID(),
                UUID.randomUUID(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                "CAMION",
                BigDecimal.valueOf(1000),
                5,
                BigDecimal.valueOf(5000),
                BigDecimal.valueOf(4500),
                "CALCULADA",
                LocalDateTime.now(),
                "user-id",
                List.of()
        );

        when(buscarUseCase.ejecutar(any(FiltroLiquidacionDTO.class), any())).thenReturn(response);

        mockMvc.perform(get("/api/liquidaciones/buscar")
                        .with(autenticado("user-id", "ROLE_GESTOR_FINANCIERO"))
                        .param("idRuta", UUID.randomUUID().toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado_liquidacion").value("CALCULADA"));
    }

    /**
     * Con addFilters=false, SecurityContextHolderAwareRequestFilter no corre,
     * por lo que request.getUserPrincipal() retorna null y Spring MVC inyecta
     * null para el parametro Authentication. Se soluciona seteando setUserPrincipal()
     * directamente en el MockHttpServletRequest ademas de SecurityContextHolder.
     */
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
