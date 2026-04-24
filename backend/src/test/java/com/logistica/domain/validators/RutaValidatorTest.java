package com.logistica.domain.validators;

import com.logistica.domain.enums.EstadoParada;
import com.logistica.domain.enums.MotivoFalla;
import com.logistica.domain.exceptions.RutaInvalidaException;
import com.logistica.domain.models.Parada;
import com.logistica.domain.models.Ruta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class RutaValidatorTest {

    private RutaValidator validator;

    @BeforeEach
    void setUp() {
        validator = new RutaValidator();
    }

    // ─────────────────────────────────────────────
    // VALIDACIÓN DE ESTRUCTURA
    // ─────────────────────────────────────────────

    @Test
    void lanza_excepcion_si_ruta_es_null() {

        assertThatThrownBy(() ->
                validator.validar(null, 1)
        ).isInstanceOf(RutaInvalidaException.class)
                .hasMessageContaining("no puede ser null");
    }

    @Test
    void lanza_excepcion_si_rutaId_es_null() {

        Ruta ruta = Ruta.builder()
                .rutaId(null)
                .paradas(List.of())
                .build();

        assertThatThrownBy(() ->
                validator.validar(ruta, 0)
        ).isInstanceOf(RutaInvalidaException.class)
                .hasMessageContaining("rutaId");
    }

    @Test
    void lanza_excepcion_si_paradas_es_null() {

        Ruta ruta = Ruta.builder()
                .rutaId(UUID.randomUUID())
                .paradas(null)
                .build();

        assertThatThrownBy(() ->
                validator.validar(ruta, 1)
        ).isInstanceOf(RutaInvalidaException.class)
                .hasMessageContaining("lista de paradas");
    }

    // ─────────────────────────────────────────────
    // CONSISTENCIA DE PARADAS
    // ─────────────────────────────────────────────

    @Test
    void lanza_excepcion_si_total_paradas_no_coincide() {

        Ruta ruta = Ruta.builder()
                .rutaId(UUID.randomUUID())
                .paradas(List.of(
                        Parada.builder().build(),
                        Parada.builder().build()
                ))
                .build();

        assertThatThrownBy(() ->
                validator.validar(ruta, 5)
        ).isInstanceOf(RutaInvalidaException.class)
                .hasMessageContaining("Total de paradas no coincide")
                .hasMessageContaining("esperadas=5")
                .hasMessageContaining("recibidas=2");
    }

    @Test
    void pasa_validacion_si_total_paradas_coincide() {

        Ruta ruta = Ruta.builder()
                .rutaId(UUID.randomUUID())
                .paradas(List.of(
                        Parada.builder().estado(EstadoParada.EXITOSA).build(),
                        Parada.builder().estado(EstadoParada.EXITOSA).build()
                ))
                .build();

        assertThatCode(() ->
                validator.validar(ruta, 2)
        ).doesNotThrowAnyException();
    }

    // ─────────────────────────────────────────────
    // INTEGRIDAD DE PARADAS
    // ─────────────────────────────────────────────

    @Test
    void lanza_excepcion_si_parada_es_null_en_lista() {

        List<Parada> paradas = new ArrayList<>();
        paradas.add(null);

        Ruta ruta = Ruta.builder()
                .rutaId(UUID.randomUUID())
                .paradas(paradas)
                .build();

        assertThatThrownBy(() ->
                validator.validar(ruta, 1)
        )
                .isInstanceOf(RutaInvalidaException.class)
                .hasMessageContaining("Parada nula");
    }

    @Test
    void lanza_excepcion_si_parada_sin_estado() {

        Parada parada = Parada.builder()
                .estado(null)
                .paradaId(UUID.randomUUID())
                .build();

        Ruta ruta = Ruta.builder()
                .rutaId(UUID.randomUUID())
                .paradas(List.of(parada))
                .build();

        assertThatThrownBy(() ->
                validator.validar(ruta, 1)
        ).isInstanceOf(RutaInvalidaException.class)
                .hasMessageContaining("sin estado");
    }

    @Test
    void lanza_excepcion_si_parada_fallida_sin_motivo() {

        Parada parada = Parada.builder()
                .estado(EstadoParada.FALLIDA)
                .paradaId(UUID.randomUUID())
                .motivoFalla(null)
                .build();

        Ruta ruta = Ruta.builder()
                .rutaId(UUID.randomUUID())
                .paradas(List.of(parada))
                .build();

        assertThatThrownBy(() ->
                validator.validar(ruta, 1)
        ).isInstanceOf(RutaInvalidaException.class)
                .hasMessageContaining("sin motivo");
    }

    // ─────────────────────────────────────────────
    // CASO COMPLETO VALIDO (FLUJO REAL)
    // ─────────────────────────────────────────────

    @Test
    void valida_ruta_completa_correctamente() {

        Parada p1 = Parada.builder()
                .estado(EstadoParada.EXITOSA)
                .paradaId(UUID.randomUUID())
                .build();

        Parada p2 = Parada.builder()
                .estado(EstadoParada.FALLIDA)
                .paradaId(UUID.randomUUID())
                .motivoFalla(MotivoFalla.CLIENTE_AUSENTE)
                .build();

        Ruta ruta = Ruta.builder()
                .rutaId(UUID.randomUUID())
                .paradas(List.of(p1, p2))
                .build();

        assertThatCode(() ->
                validator.validar(ruta, 2)
        ).doesNotThrowAnyException();
    }

    // ─────────────────────────────────────────────
    // EDGE CASES REALES
    // ─────────────────────────────────────────────

    @Test
    void valida_ruta_con_muchas_paradas() {

        List<Parada> paradas = java.util.stream.IntStream.range(0, 50)
                .mapToObj(i -> Parada.builder()
                        .estado(EstadoParada.EXITOSA)
                        .paradaId(UUID.randomUUID())
                        .build()
                )
                .toList();

        Ruta ruta = Ruta.builder()
                .rutaId(UUID.randomUUID())
                .paradas(paradas)
                .build();

        assertThatCode(() ->
                validator.validar(ruta, 50)
        ).doesNotThrowAnyException();
    }
}