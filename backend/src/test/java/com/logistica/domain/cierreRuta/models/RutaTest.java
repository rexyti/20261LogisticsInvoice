package com.logistica.domain.cierreRuta.models;

import com.logistica.domain.cierreRuta.enums.EstadoParada;
import com.logistica.domain.cierreRuta.enums.EstadoProcesamiento;
import com.logistica.domain.shared.enums.TipoVehiculo;
import com.logistica.domain.cierreRuta.exceptions.RutaInvalidaException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RutaTest {

    private TransportistaRuta transportistaValido() {
        return new TransportistaRuta(UUID.randomUUID(), "Transportista Test");
    }

    private Parada paradaValida() {
        return Parada.builder()
                .paradaId(UUID.randomUUID())
                .paqueteId(UUID.randomUUID())
                .estado(EstadoParada.EXITOSA)
                .build();
    }

    @Test
    void deberia_procesar_ruta_valida_correctamente() {

        RutaCerrada ruta = RutaCerrada.builder()
                .rutaId(UUID.randomUUID())
                .transportista(transportistaValido())
                .tipoVehiculo(TipoVehiculo.NHR)
                .modeloContrato("STANDARD")
                .paradas(List.of(paradaValida()))
                .build();

        LocalDateTime ahora = LocalDateTime.of(2024, 1, 1, 0, 0);

        ruta.procesar(ahora);

        assertEquals(EstadoProcesamiento.OK, ruta.getEstadoProcesamiento());
        assertFalse(ruta.obtenerEventos().isEmpty());
    }

    @Test
    void deberia_fallar_si_ruta_no_tiene_id() {

        RutaCerrada ruta = RutaCerrada.builder()
                .transportista(transportistaValido())
                .tipoVehiculo(TipoVehiculo.NHR)
                .modeloContrato("STANDARD")
                .paradas(List.of(paradaValida()))
                .build();

        assertThrows(RutaInvalidaException.class,
                () -> ruta.procesar(LocalDateTime.now()));
    }

    @Test
    void deberia_fallar_si_no_tiene_paradas() {

        RutaCerrada ruta = RutaCerrada.builder()
                .rutaId(UUID.randomUUID())
                .transportista(transportistaValido())
                .tipoVehiculo(TipoVehiculo.NHR)
                .modeloContrato("STANDARD")
                .paradas(List.of())
                .build();

        assertThrows(RutaInvalidaException.class,
                () -> ruta.procesar(LocalDateTime.now()));
    }

    @Test
    void deberia_fallar_si_hay_parada_nula() {

        RutaCerrada ruta = RutaCerrada.builder()
                .rutaId(UUID.randomUUID())
                .transportista(transportistaValido())
                .tipoVehiculo(TipoVehiculo.NHR)
                .modeloContrato("STANDARD")
                .paradas(Arrays.asList((Parada) null))
                .build();

        assertThrows(RutaInvalidaException.class,
                () -> ruta.procesar(LocalDateTime.now()));
    }

    @Test
    void deberia_fallar_si_parada_no_tiene_estado() {

        Parada parada = Parada.builder()
                .paradaId(UUID.randomUUID())
                .paqueteId(UUID.randomUUID())
                .estado(null)
                .build();

        RutaCerrada ruta = RutaCerrada.builder()
                .rutaId(UUID.randomUUID())
                .transportista(transportistaValido())
                .tipoVehiculo(TipoVehiculo.NHR)
                .modeloContrato("STANDARD")
                .paradas(List.of(parada))
                .build();

        assertThrows(RutaInvalidaException.class,
                () -> ruta.procesar(LocalDateTime.now()));
    }

    @Test
    void deberia_fallar_si_parada_fallida_sin_motivo() {

        Parada parada = Parada.builder()
                .paradaId(UUID.randomUUID())
                .paqueteId(UUID.randomUUID())
                .estado(EstadoParada.FALLIDA)
                .motivoFalla(null)
                .build();

        RutaCerrada ruta = RutaCerrada.builder()
                .rutaId(UUID.randomUUID())
                .transportista(transportistaValido())
                .tipoVehiculo(TipoVehiculo.NHR)
                .modeloContrato("STANDARD")
                .paradas(List.of(parada))
                .build();

        assertThrows(RutaInvalidaException.class,
                () -> ruta.procesar(LocalDateTime.now()));
    }

    @Test
    void deberia_marcar_revision_si_contrato_es_nulo() {

        RutaCerrada ruta = RutaCerrada.builder()
                .rutaId(UUID.randomUUID())
                .transportista(transportistaValido())
                .tipoVehiculo(TipoVehiculo.NHR)
                .modeloContrato(null)
                .paradas(List.of(paradaValida()))
                .build();

        ruta.procesar(LocalDateTime.now());

        assertEquals(EstadoProcesamiento.REQUIERE_REVISION, ruta.getEstadoProcesamiento());
    }

    @Test
    void deberia_marcar_revision_si_tipo_vehiculo_es_nulo() {

        RutaCerrada ruta = RutaCerrada.builder()
                .rutaId(UUID.randomUUID())
                .transportista(transportistaValido())
                .tipoVehiculo(null)
                .modeloContrato("STANDARD")
                .paradas(List.of(paradaValida()))
                .build();

        ruta.procesar(LocalDateTime.now());

        assertEquals(EstadoProcesamiento.REQUIERE_REVISION, ruta.getEstadoProcesamiento());
    }
}
