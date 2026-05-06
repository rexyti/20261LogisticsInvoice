package com.logistica.infrastructure.web.controllers;

import com.logistica.application.cierreRuta.dtos.response.RutaProcesadaResponseDTO;
import com.logistica.application.cierreRuta.usecases.ruta.ConsultarRutaUseCase;
import com.logistica.domain.cierreRuta.exceptions.RutaNotFoundException;
import com.logistica.infrastructure.cierreRuta.config.SecurityConfig;
import com.logistica.infrastructure.cierreRuta.web.controllers.RutaController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RutaController.class)
@AutoConfigureMockMvc(addFilters = false)
class RutaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SecurityConfig cierreRutaSecurityConfig;

    @MockBean
    private ConsultarRutaUseCase consultarRutaUseCase;

    // ─────────────────────────────────────────────
    // GET /{id}
    // ─────────────────────────────────────────────


    @Test
    @DisplayName("Debe retornar 200 y la ruta cuando existe")
    void debe_obtener_ruta_ok() throws Exception {

        UUID id = UUID.randomUUID();

        RutaProcesadaResponseDTO response = RutaProcesadaResponseDTO.builder()
                .rutaId(id)
                .build();

        when(consultarRutaUseCase.ejecutar(id)).thenReturn(response);

        mockMvc.perform(get("/api/v1/rutas/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ruta_id").value(id.toString()));

        verify(consultarRutaUseCase).ejecutar(id);
    }

    @Test
    @DisplayName("Debe retornar 404 cuando la ruta no existe")
    void debe_retornar_404_si_no_existe() throws Exception {

        UUID id = UUID.randomUUID();

        when(consultarRutaUseCase.ejecutar(id))
                .thenThrow(new RutaNotFoundException(id));

        mockMvc.perform(get("/api/v1/rutas/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Ruta no encontrada: " + id));

        verify(consultarRutaUseCase).ejecutar(id);
    }

    // ─────────────────────────────────────────────
    // GET /
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("Debe listar rutas correctamente")
    void debe_listar_rutas() throws Exception {

        RutaProcesadaResponseDTO r1 = RutaProcesadaResponseDTO.builder()
                .rutaId(UUID.randomUUID())
                .build();

        RutaProcesadaResponseDTO r2 = RutaProcesadaResponseDTO.builder()
                .rutaId(UUID.randomUUID())
                .build();

        when(consultarRutaUseCase.listarTodas(isNull(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(r1, r2)));

        mockMvc.perform(get("/api/v1/rutas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));

        verify(consultarRutaUseCase).listarTodas(isNull(), any(Pageable.class));
    }

    @Test
    @DisplayName("Debe retornar lista vacía si no hay rutas")
    void debe_retornar_lista_vacia() throws Exception {

        when(consultarRutaUseCase.listarTodas(isNull(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/v1/rutas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));

        verify(consultarRutaUseCase).listarTodas(isNull(), any(Pageable.class));
    }
}
