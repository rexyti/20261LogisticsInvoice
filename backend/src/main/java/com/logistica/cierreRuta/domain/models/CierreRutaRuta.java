package com.logistica.cierreRuta.domain.models;

import com.logistica.cierreRuta.domain.enums.EstadoParada;
import com.logistica.cierreRuta.domain.enums.EstadoProcesamiento;
import com.logistica.cierreRuta.domain.enums.TipoAlertaRuta;
import com.logistica.cierreRuta.domain.enums.CierreRutaTipoVehiculo;
import com.logistica.cierreRuta.domain.events.RutaCerradaProcesadaEvent;
import com.logistica.cierreRuta.domain.exceptions.RutaInvalidaException;
import com.logistica.cierreRuta.domain.ports.DomainEvent;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class CierreRutaRuta {

    private UUID rutaId;
    private CierreRutaTransportista transportista;
    private UUID vehiculoId;
    private CierreRutaTipoVehiculo tipoVehiculo;
    private String modeloContrato;
    private LocalDateTime fechaInicioTransito;
    private LocalDateTime fechaCierre;
    private EstadoProcesamiento estadoProcesamiento;

    @Singular
    private List<Parada> paradas;

    //  Eventos de dominio
    private final List<DomainEvent> eventos = new ArrayList<>();

    // =============================
    //  MÉTODO PRINCIPAL
    // =============================
    public void procesar(LocalDateTime ahora) {

        validarEstructura();
        validarParadas();

        EstadoProcesamiento estado = evaluarEstado();
        this.estadoProcesamiento = estado;

        generarEventos(estado, ahora);
    }

    // =============================
    //  VALIDACIONES
    // =============================
    private void validarEstructura() {

        if (rutaId == null) {
            throw new RutaInvalidaException("Ruta sin id");
        }

        if(transportista == null) {
            throw new RutaInvalidaException("CierreRutaTransportista sin id");
        }

        if (paradas == null || paradas.isEmpty()) {
            throw new RutaInvalidaException("Ruta sin paradas");
        }
    }

    private void validarParadas() {

        for (Parada parada : paradas) {

            if (parada == null) {
                throw new RutaInvalidaException("Parada nula en rutaId: " + rutaId);
            }

            if (parada.getEstado() == null) {
                throw new RutaInvalidaException(
                        "Parada sin estado. paradaId: " + parada.getParadaId()
                );
            }

            if (parada.getEstado() == EstadoParada.FALLIDA
                    && parada.getMotivoFalla() == null) {
                throw new RutaInvalidaException(
                        "Parada fallida sin motivo. paradaId: " + parada.getParadaId()
                );
            }

            if (parada.getPaqueteId() == null) {
                throw new RutaInvalidaException(
                        "Parada sin paqueteId. paradaId: " + parada.getParadaId()
                                + " en rutaId: " + rutaId
                );
            }
        }
    }

    // ============================
    // LÓGICA DE NEGOCIO
    // ============================
    private EstadoProcesamiento evaluarEstado() {

        if (modeloContrato == null) {
            eventos.add(crearEvento(
                    EstadoProcesamiento.REQUIERE_REVISION,
                    TipoAlertaRuta.CONTRATO_NULO,
                    "Contrato no encontrado",
                    null
            ));
            return EstadoProcesamiento.REQUIERE_REVISION;
        }

        if (tipoVehiculo == null) {
            eventos.add(crearEvento(
                    EstadoProcesamiento.REQUIERE_REVISION,
                    TipoAlertaRuta.VEHICULO_DESCONOCIDO,
                    "Vehículo desconocido",
                    null
            ));
            return EstadoProcesamiento.REQUIERE_REVISION;
        }



        return EstadoProcesamiento.OK;
    }

    private void generarEventos(EstadoProcesamiento estado, LocalDateTime ahora) {

        if (estado == EstadoProcesamiento.OK) {
            eventos.add(crearEvento(
                    estado,
                    null,
                    "Ruta procesada correctamente",
                    ahora
            ));
        }
    }

    // =============================
    // EVENTOS
    // =============================
    private RutaCerradaProcesadaEvent crearEvento(
            EstadoProcesamiento estado,
            TipoAlertaRuta alerta,
            String descripcion,
            LocalDateTime fecha
    ) {
        return new RutaCerradaProcesadaEvent(
                this.rutaId,
                estado,
                alerta,
                descripcion,
                fecha
        );
    }

    public List<DomainEvent> obtenerEventos() {
        return List.copyOf(eventos);
    }

    public void limpiarEventos() {
        eventos.clear();
    }

    public void asignarTransportista(CierreRutaTransportista transportista) {
        this.transportista = transportista;
    }
}
