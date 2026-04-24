package com.logistica.liquidacion.domain.models;

import com.logistica.liquidacion.domain.enums.EstadoPaquete;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
public class Paquete {

    private final UUID id;
    private final EstadoPaquete estadoFinal;
    private final String novedades;

    public static class PaqueteBuilder {
        public Paquete build() {

            if (id == null) {
                throw new IllegalArgumentException("El id del paquete no puede ser null");
            }

            if (estadoFinal == null) {
                throw new IllegalArgumentException("El estado final es obligatorio");
            }

            return new Paquete(id, estadoFinal, novedades);
        }
    }

    private Paquete(UUID id, EstadoPaquete estadoFinal, String novedades) {
        this.id = id;
        this.estadoFinal = estadoFinal;
        this.novedades = novedades;
    }

    public boolean esEntregado() {
        return estadoFinal == EstadoPaquete.ENTREGADO;
    }

    public boolean esFallidoCliente() {
        return estadoFinal == EstadoPaquete.FALLIDO_CLIENTE;
    }

    public boolean esFallidoTransportista() {
        return estadoFinal == EstadoPaquete.FALLIDO_TRANSPORTISTA;
    }

    public boolean esValidoParaCalculo() {
        return estadoFinal != null;
    }

    public BigDecimal obtenerFactorPago(BigDecimal porcentajeFallidoCliente) {
        if (esEntregado()) {
            return BigDecimal.ONE;
        }
        if (esFallidoCliente()) {
            return porcentajeFallidoCliente;
        }
        return BigDecimal.ZERO;
    }
    public boolean tieneReglaDePagoAplicable() {
        return esValidoParaCalculo();
    }
}