package com.logistica.unit;

import com.logistica.domain.enums.EstadoPaquete;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class EstadoPaqueteTest {

    // T008: verificar que cada estado mapea al porcentaje correcto (FR-002)
    @ParameterizedTest(name = "{0} -> {1}%")
    @CsvSource({
        "ENTREGADO, 100",
        "DEVUELTO,   50",
        "DAÑADO,      0",
        "EXTRAVIADO,  0"
    })
    void cadaEstadoMapeoAlPorcentajeCorrecto(String estado, int porcentajeEsperado) {
        EstadoPaquete estadoPaquete = EstadoPaquete.valueOf(estado);
        assertEquals(porcentajeEsperado, estadoPaquete.getPorcentajePago());
    }

    @Test
    void fromStringDevuelveEntregadoParaMinusculas() {
        Optional<EstadoPaquete> resultado = EstadoPaquete.fromString("entregado");
        assertTrue(resultado.isPresent());
        assertEquals(EstadoPaquete.ENTREGADO, resultado.get());
    }

    @Test
    void fromStringDevuelveEmptyParaEstadoDesconocido() {
        Optional<EstadoPaquete> resultado = EstadoPaquete.fromString("EN_INSPECCION");
        assertTrue(resultado.isEmpty());
    }

    @Test
    void fromStringDevuelveEmptyParaNull() {
        Optional<EstadoPaquete> resultado = EstadoPaquete.fromString(null);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void pendienteSincronizacionNoEsMapeableDesdeExterior() {
        // PENDIENTE_SINCRONIZACION es estado interno; no debe mapearse desde respuesta del Módulo de Gestión
        Optional<EstadoPaquete> resultado = EstadoPaquete.fromString("PENDIENTE_SINCRONIZACION");
        assertTrue(resultado.isEmpty());
    }
}
