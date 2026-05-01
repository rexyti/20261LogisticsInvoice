package com.logistica.cierreRuta.domain.models;

import com.logistica.cierreRuta.domain.enums.EstadoParada;
import com.logistica.cierreRuta.domain.enums.EstadoProcesamiento;
import com.logistica.cierreRuta.domain.enums.CierreRutaTipoVehiculo;
import com.logistica.cierreRuta.domain.exceptions.RutaInvalidaException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RutaTest {

    //Metodo Auxiliar
    private Parada paradaValida() {
        return Parada.builder()
                .paradaId(UUID.randomUUID())
                .paqueteId(UUID.randomUUID())
                .estado(EstadoParada.EXITOSA)
                .build();
    }


    // CASO FELIZ
    @Test
    void deberia_procesar_ruta_valida_correctamente() {

        Ruta ruta = Ruta.builder()
                .rutaId(UUID.randomUUID())
                .tipoVehiculo(CierreRutaTipoVehiculo.NHR)
                .modeloContrato("STANDARD")
                .paradas(List.of(paradaValida()))
                .build();

        LocalDateTime ahora = LocalDateTime.of(2024,1,1,0,0);

        ruta.procesar(ahora);

        assertEquals(EstadoProcesamiento.OK, ruta.getEstadoProcesamiento());
        assertFalse(ruta.obtenerEventos().isEmpty());
    }

    // VALIDACIONES

    @Test
    void deberia_fallar_si_ruta_no_tiene_id() {

        Ruta ruta = Ruta.builder()
                .tipoVehiculo(CierreRutaTipoVehiculo.NHR)
                .modeloContrato("STANDARD")
                .paradas(List.of(paradaValida()))
                .build();

        assertThrows(RutaInvalidaException.class,
                () -> ruta.procesar(LocalDateTime.now()));
    }

    @Test
    void deberia_fallar_si_no_tiene_paradas() {

        Ruta ruta = Ruta.builder()
                .rutaId(UUID.randomUUID())
                .tipoVehiculo(CierreRutaTipoVehiculo.NHR)
                .modeloContrato("STANDARD")
                .paradas(List.of())
                .build();

        assertThrows(RutaInvalidaException.class,
                () -> ruta.procesar(LocalDateTime.now()));
    }

    @Test
    void deberia_fallar_si_hay_parada_nula() {

        Ruta ruta = Ruta.builder()
                .rutaId(UUID.randomUUID())
                .tipoVehiculo(CierreRutaTipoVehiculo.NHR)
                .modeloContrato("STANDARD")
                .paradas(List.of((Parada) null))
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

        Ruta ruta = Ruta.builder()
                .rutaId(UUID.randomUUID())
                .tipoVehiculo(CierreRutaTipoVehiculo.NHR)
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

        Ruta ruta = Ruta.builder()
                .rutaId(UUID.randomUUID())
                .tipoVehiculo(CierreRutaTipoVehiculo.NHR)
                .modeloContrato("STANDARD")
                .paradas(List.of(parada))
                .build();

        assertThrows(RutaInvalidaException.class,
                () -> ruta.procesar(LocalDateTime.now()));
    }

    @Test
    void deberia_marcar_revision_si_contrato_es_nulo() {

        Ruta ruta = Ruta.builder()
                .rutaId(UUID.randomUUID())
                .tipoVehiculo(CierreRutaTipoVehiculo.NHR)
                .modeloContrato(null)
                .paradas(List.of(paradaValida()))
                .build();

        ruta.procesar(LocalDateTime.now());

        assertEquals(EstadoProcesamiento.REQUIERE_REVISION, ruta.getEstadoProcesamiento());
    }

    @Test
    void deberia_marcar_revision_si_tipo_vehiculo_es_nulo() {

        Ruta ruta = Ruta.builder()
                .rutaId(UUID.randomUUID())
                .tipoVehiculo(null)
                .modeloContrato("STANDARD")
                .paradas(List.of(paradaValida()))
                .build();

        ruta.procesar(LocalDateTime.now());

        assertEquals(EstadoProcesamiento.REQUIERE_REVISION, ruta.getEstadoProcesamiento());
    }

}