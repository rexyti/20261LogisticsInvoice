package com.logistica.domain.services;

import com.logistica.cierreRuta.domain.enums.EstadoParada;
import com.logistica.cierreRuta.domain.enums.MotivoFalla;
import com.logistica.cierreRuta.domain.enums.ResponsableFalla;
import com.logistica.cierreRuta.domain.exceptions.ParadaInvalidaException;
import com.logistica.cierreRuta.domain.models.Parada;
import com.logistica.cierreRuta.domain.models.CierreRutaRuta;
import com.logistica.cierreRuta.domain.services.ClasificacionRutaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class ClasificacionRutaServiceTest {

    private ClasificacionRutaService service;

    @BeforeEach
    void setUp() {
        service = new ClasificacionRutaService();
    }



    private CierreRutaRuta ruta(List<Parada> paradas) {
        return CierreRutaRuta.builder()
                .rutaId(UUID.randomUUID())
                .paradas(paradas)
                .build();
    }

    private Parada paradaFallida(MotivoFalla motivo) {
        return Parada.builder()
                .paradaId(UUID.randomUUID())
                .estado(EstadoParada.FALLIDA)
                .motivoFalla(motivo)
                .build();
    }

    private Parada paradaExitosa() {
        return Parada.builder()
                .paradaId(UUID.randomUUID())
                .estado(EstadoParada.EXITOSA)
                .motivoFalla(null)
                .build();
    }



    @Test
    void ignoraSilenciosamenteCuandoRutaEsNull() {
        // El servicio tolera null — el consumer valida antes de llegar aquí
        assertThatCode(() -> service.clasificar(null))
                .doesNotThrowAnyException();
    }

    @Test
    void noFallaCuandoListaParadasVacia() {
        assertThatCode(() -> service.clasificar(ruta(List.of())))
                .doesNotThrowAnyException();
    }



    @Test
    void lanzaExcepcionCuandoParadaEsNull() {
        List<Parada> paradas = new ArrayList<>();
        paradas.add(null);

        assertThatThrownBy(() -> service.clasificar(ruta(paradas)))
                .isInstanceOf(ParadaInvalidaException.class)
                .hasMessageContaining("Parada inválida");
    }

    @Test
    void noSePuedeConstruirParadaFallidaSinMotivo() {

        UUID id = UUID.randomUUID();

        assertThatThrownBy(() -> Parada.crear(id, EstadoParada.FALLIDA, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Parada fallida sin motivo. paradaId: " + id);
    }



    @ParameterizedTest(name = "{0} → responsable: {1}")
    @MethodSource("motivosYResponsables")
    void clasificaResponsableCorrectamentePorMotivo(
            MotivoFalla motivo,
            ResponsableFalla responsableEsperado) {

        Parada p = paradaFallida(motivo);
        CierreRutaRuta r = ruta(List.of(p));

        assertThatCode(() -> service.clasificar(r))
                .doesNotThrowAnyException();

        assertThat(p.getMotivoFalla().getResponsable())
                .isEqualTo(responsableEsperado);
    }

    static Stream<Arguments> motivosYResponsables() {
        return Stream.of(
                Arguments.of(MotivoFalla.CLIENTE_AUSENTE,     ResponsableFalla.CLIENTE),
                Arguments.of(MotivoFalla.DIRECCION_ERRONEA,   ResponsableFalla.CLIENTE),
                Arguments.of(MotivoFalla.RECHAZADO,           ResponsableFalla.CLIENTE),
                Arguments.of(MotivoFalla.ZONA_DIFICIL_ACCESO, ResponsableFalla.EMPRESA),
                Arguments.of(MotivoFalla.PAQUETE_DANADO,      ResponsableFalla.TRANSPORTISTA),
                Arguments.of(MotivoFalla.PERDIDA_PAQUETE,     ResponsableFalla.TRANSPORTISTA)
        );
    }



    @Test
    void noFallaConParadaExitosa() {
        assertThatCode(() -> service.clasificar(ruta(List.of(paradaExitosa()))))
                .doesNotThrowAnyException();
    }


    @Test
    void mezclaParadasValidasProcesaCorrectamente() {
        Parada p1 = paradaFallida(MotivoFalla.CLIENTE_AUSENTE);
        Parada p2 = paradaFallida(MotivoFalla.PAQUETE_DANADO);
        Parada p3 = paradaExitosa();

        CierreRutaRuta r = ruta(List.of(p1, p2, p3));

        assertThatCode(() -> service.clasificar(r))
                .doesNotThrowAnyException();


        assertThat(service.contarParadasPorResponsable(r.getParadas(), ResponsableFalla.CLIENTE))
                .isEqualTo(1);
        assertThat(service.contarParadasPorResponsable(r.getParadas(), ResponsableFalla.TRANSPORTISTA))
                .isEqualTo(1);
    }



    @Test
    void contarRetornaCeroConListaNull() {
        assertThat(service.contarParadasPorResponsable(null, ResponsableFalla.CLIENTE))
                .isZero();
    }

    @Test
    void contarRetornaCeroConListaVacia() {
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

        assertThat(service.contarParadasPorResponsable(paradas, ResponsableFalla.CLIENTE))
                .isEqualTo(1);
        assertThat(service.contarParadasPorResponsable(paradas, ResponsableFalla.TRANSPORTISTA))
                .isEqualTo(1);
    }

    @Test
    void contarExcluyeParadasSinMotivo() {

        Parada p = Parada.builder()
                .paradaId(UUID.randomUUID())
                .estado(EstadoParada.FALLIDA)
                .motivoFalla(null)
                .build();

        assertThat(service.contarParadasPorResponsable(List.of(p), ResponsableFalla.CLIENTE))
                .isZero();
    }
}