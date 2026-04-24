package com.logistica.liquidacion.domain.models;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class Ruta {

    private final UUID id;
    private final OffsetDateTime fechaInicio;
    private final OffsetDateTime fechaCierre;
    private final List<Paquete> paquetes;

    public static class RutaBuilder {
        public Ruta build() {

            if (id == null) {
                throw new IllegalArgumentException("El id de la ruta no puede ser null");
            }

            if (fechaInicio != null && fechaCierre.isBefore(fechaInicio)) {
                throw new IllegalArgumentException("La fecha de cierre no puede ser anterior a la fecha de inicio");
            }

            if (fechaCierre == null) {
                throw new IllegalArgumentException("La fecha de cierre es obligatoria");
            }

            if (fechaCierre.isBefore(fechaInicio)) {
                throw new IllegalArgumentException("La fecha de cierre no puede ser anterior a la fecha de inicio");
            }

            if (paquetes == null) {
                throw new IllegalArgumentException("La lista de paquetes no puede ser null");
            }

            return new Ruta(id, fechaInicio, fechaCierre, paquetes);
        }
    }

    private Ruta(UUID id, OffsetDateTime fechaInicio, OffsetDateTime fechaCierre, List<Paquete> paquetes) {
        this.id = id;
        this.fechaInicio = fechaInicio;
        this.fechaCierre = fechaCierre;
        this.paquetes = paquetes;
    }

    public boolean tienePaquetes() {
        return paquetes != null && !paquetes.isEmpty();
    }

    public int totalPaquetes() {
        return paquetes.size();
    }

    public long paquetesEntregados() {
        return paquetes.stream().filter(Paquete::esEntregado).count();
    }

    public long paquetesFallidosCliente() {
        return paquetes.stream().filter(Paquete::esFallidoCliente).count();
    }

    public long paquetesFallidosTransportista() {
        return paquetes.stream().filter(Paquete::esFallidoTransportista).count();
    }
    public List<Paquete> obtenerPaquetesValidos() {
        return paquetes.stream()
                .filter(Paquete::tieneReglaDePagoAplicable)
                .toList();
    }

    public boolean fueCompletada() {
        return tienePaquetes() && paquetes.stream().allMatch(Paquete::esEntregado);
    }
}
