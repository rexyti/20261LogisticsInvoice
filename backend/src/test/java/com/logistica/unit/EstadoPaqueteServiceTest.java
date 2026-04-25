package com.logistica.unit;

import com.logistica.domain.enums.EstadoPaquete;
import com.logistica.domain.services.EstadoPaqueteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class EstadoPaqueteServiceTest {

    private EstadoPaqueteService service;

    @BeforeEach
    void setUp() {
        service = new EstadoPaqueteService();
    }

    // Mapeo de estados válidos con porcentaje esperado (FR-002)
    @ParameterizedTest(name = "mapear '{0}' -> {1}%")
    @CsvSource({
        "ENTREGADO, 100",
        "DEVUELTO,   50",
        "DAÑADO,      0",
        "EXTRAVIADO,  0"
    })
    void mapearEstado_estadoFinanciero_retornaPresente(String estadoStr, int porcentajeEsperado) {
        Optional<EstadoPaquete> resultado = service.mapearEstado(estadoStr);
        assertTrue(resultado.isPresent());
        assertEquals(porcentajeEsperado, service.calcularPorcentajePago(resultado.get()));
    }

    @Test
    void mapearEstado_estadoDesconocido_retornaEmpty() {
        assertTrue(service.mapearEstado("EN_INSPECCION").isEmpty());
    }

    @Test
    void mapearEstado_null_retornaEmpty() {
        assertTrue(service.mapearEstado(null).isEmpty());
    }

    @Test
    void mapearEstado_pendienteSincronizacion_retornaEmpty() {
        // Estado interno: no debe mapearse desde el módulo externo
        assertTrue(service.mapearEstado("PENDIENTE_SINCRONIZACION").isEmpty());
    }

    @Test
    void calcularPorcentajePago_entregado_retorna100() {
        assertEquals(100, service.calcularPorcentajePago(EstadoPaquete.ENTREGADO));
    }

    @Test
    void calcularPorcentajePago_devuelto_retorna50() {
        assertEquals(50, service.calcularPorcentajePago(EstadoPaquete.DEVUELTO));
    }

    @Test
    void calcularPorcentajePago_danado_retorna0() {
        assertEquals(0, service.calcularPorcentajePago(EstadoPaquete.DAÑADO));
    }

    @Test
    void calcularPorcentajePago_extraviado_retorna0() {
        assertEquals(0, service.calcularPorcentajePago(EstadoPaquete.EXTRAVIADO));
    }
}
