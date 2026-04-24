package com.logistica.domain.services;

import com.logistica.domain.enums.EstadoParada;
import com.logistica.domain.enums.MotivoFalla;
import com.logistica.domain.enums.ResponsableFalla;
import com.logistica.domain.exceptions.ParadaInvalidaException;
import com.logistica.domain.models.Parada;
import com.logistica.domain.models.Ruta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class ClasificacionRutaServiceTest {

    private ClasificacionRutaService service;

    @BeforeEach
    void setUp() {
        service = new ClasificacionRutaService();
    }

    // ─────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────

    private Ruta ruta(List<Parada> paradas) {
        return Ruta.builder()
                .rutaId(UUID.randomUUID())
                .paradas(paradas)
                .build();
    }

    private Parada paradaFallida(MotivoFalla motivo) {
        return Parada.builder()
                .estado(EstadoParada.FALLIDA)
                .motivoFalla(motivo)
                .build();
    }

    private Parada paradaExitosa() {
        return Parada.builder()
                .estado(EstadoParada.EXITOSA)
                .motivoFalla(null)
                .build();
    }

    // ─────────────────────────────────────────────
    // CASOS BASE (DEFENSIVOS REALES)
    // ─────────────────────────────────────────────

    @Test
    void noFallaCuandoRutaEsNull() {
        assertThatCode(() -> service.clasificar(null))
                .doesNotThrowAnyException();
    }

    @Test
    void noFallaCuandoListaParadasVacia() {
        Ruta r = ruta(List.of());

        assertThatCode(() -> service.clasificar(r))
                .doesNotThrowAnyException();
    }

    // ─────────────────────────────────────────────
    // VALIDACIONES CRÍTICAS
    // ─────────────────────────────────────────────


    @Test
    void lanzaExcepcionCuandoParadaEsNull() {

        List<Parada> paradas = new ArrayList<>();
        paradas.add(null);

        Ruta r = ruta(paradas);

        assertThatThrownBy(() -> service.clasificar(r))
                .isInstanceOf(ParadaInvalidaException.class)
                .hasMessageContaining("Parada inválida");
    }

    @Test
    void lanzaExcepcionCuandoParadaFallidaSinMotivo() {

        Parada p = Parada.builder()
                .estado(EstadoParada.FALLIDA)
                .motivoFalla(null)
                .build();

        Ruta r = ruta(List.of(p));

        assertThatThrownBy(() -> service.clasificar(r))
                .isInstanceOf(ParadaInvalidaException.class)
                .hasMessageContaining("sin motivoFalla");
    }

    // ─────────────────────────────────────────────
    // CASOS FELICES
    // ─────────────────────────────────────────────

    @Test
    void clasificaParadaFallidaCorrecta() {

        Parada p = paradaFallida(MotivoFalla.CLIENTE_AUSENTE);
        Ruta r = ruta(List.of(p));

        assertThatCode(() -> service.clasificar(r))
                .doesNotThrowAnyException();

        assertThat(p.getMotivoFalla().getResponsable())
                .isEqualTo(ResponsableFalla.CLIENTE);
    }

    @Test
    void noFallaConParadaExitosa() {

        Ruta r = ruta(List.of(paradaExitosa()));

        assertThatCode(() -> service.clasificar(r))
                .doesNotThrowAnyException();
    }

    // ─────────────────────────────────────────────
    // EDGE CASES REALES
    // ─────────────────────────────────────────────

    @Test
    void mezclaParadasValidasProcesaCorrectamente() {

        Parada p1 = paradaFallida(MotivoFalla.CLIENTE_AUSENTE);
        Parada p2 = paradaFallida(MotivoFalla.PAQUETE_DANADO);
        Parada p3 = paradaExitosa();

        Ruta r = ruta(List.of(p1, p2, p3));

        assertThatCode(() -> service.clasificar(r))
                .doesNotThrowAnyException();

        long totalCliente = service.contarParadasPorResponsable(
                r.getParadas(),
                ResponsableFalla.CLIENTE
        );

        assertThat(totalCliente).isEqualTo(1);
    }

    // ─────────────────────────────────────────────
    // MÉTODO DE CONTEO
    // ─────────────────────────────────────────────

    @Test
    void contarNullList() {
        assertThat(service.contarParadasPorResponsable(null, ResponsableFalla.CLIENTE))
                .isZero();
    }

    @Test
    void contarListaVacia() {
        assertThat(service.contarParadasPorResponsable(List.of(), ResponsableFalla.CLIENTE))
                .isZero();
    }

    @Test
    void contarFiltradoCorrecto() {

        List<Parada> paradas = List.of(
                paradaFallida(MotivoFalla.CLIENTE_AUSENTE),
                paradaFallida(MotivoFalla.PAQUETE_DANADO),
                paradaExitosa()
        );

        long result = service.contarParadasPorResponsable(
                paradas,
                ResponsableFalla.CLIENTE
        );

        assertThat(result).isEqualTo(1);
    }

    @Test
    void excluirParadasSinMotivo() {

        Parada p = Parada.builder()
                .estado(EstadoParada.FALLIDA)
                .motivoFalla(null)
                .build();

        long result = service.contarParadasPorResponsable(
                List.of(p),
                ResponsableFalla.CLIENTE
        );

        assertThat(result).isZero();
    }
}