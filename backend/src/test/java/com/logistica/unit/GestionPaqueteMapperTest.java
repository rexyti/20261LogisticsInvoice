package com.logistica.unit;

import com.logistica.domain.enums.EstadoPaquete;
import com.logistica.infrastructure.http.dto.GestionPaqueteDTO;
import com.logistica.infrastructure.http.mappers.GestionPaqueteMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class GestionPaqueteMapperTest {

    private GestionPaqueteMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new GestionPaqueteMapper();
    }

    @Test
    void mapearEstado_entregado_retornaEntregado() {
        GestionPaqueteDTO dto = new GestionPaqueteDTO("id", "ENTREGADO");
        Optional<EstadoPaquete> resultado = mapper.mapearEstado(dto);
        assertTrue(resultado.isPresent());
        assertEquals(EstadoPaquete.ENTREGADO, resultado.get());
    }

    @Test
    void mapearEstado_devuelto_retornaDevuelto() {
        GestionPaqueteDTO dto = new GestionPaqueteDTO("id", "DEVUELTO");
        Optional<EstadoPaquete> resultado = mapper.mapearEstado(dto);
        assertTrue(resultado.isPresent());
        assertEquals(EstadoPaquete.DEVUELTO, resultado.get());
    }

    @Test
    void mapearEstado_danado_retornaDanado() {
        GestionPaqueteDTO dto = new GestionPaqueteDTO("id", "DAÑADO");
        Optional<EstadoPaquete> resultado = mapper.mapearEstado(dto);
        assertTrue(resultado.isPresent());
        assertEquals(EstadoPaquete.DAÑADO, resultado.get());
    }

    @Test
    void mapearEstado_extraviado_retornaExtraviado() {
        GestionPaqueteDTO dto = new GestionPaqueteDTO("id", "EXTRAVIADO");
        Optional<EstadoPaquete> resultado = mapper.mapearEstado(dto);
        assertTrue(resultado.isPresent());
        assertEquals(EstadoPaquete.EXTRAVIADO, resultado.get());
    }

    // Estado en minúsculas debe mapearse correctamente (case-insensitive)
    @Test
    void mapearEstado_minusculas_retornaEstadoCorrecto() {
        GestionPaqueteDTO dto = new GestionPaqueteDTO("id", "entregado");
        Optional<EstadoPaquete> resultado = mapper.mapearEstado(dto);
        assertTrue(resultado.isPresent());
        assertEquals(EstadoPaquete.ENTREGADO, resultado.get());
    }

    @Test
    void mapearEstado_estadoDesconocido_retornaEmpty() {
        GestionPaqueteDTO dto = new GestionPaqueteDTO("id", "EN_INSPECCION");
        assertTrue(mapper.mapearEstado(dto).isEmpty());
    }

    // PENDIENTE_SINCRONIZACION es un estado interno: no debe poder mapearse desde el exterior
    @Test
    void mapearEstado_pendienteSincronizacion_retornaEmpty() {
        GestionPaqueteDTO dto = new GestionPaqueteDTO("id", "PENDIENTE_SINCRONIZACION");
        assertTrue(mapper.mapearEstado(dto).isEmpty());
    }
}
