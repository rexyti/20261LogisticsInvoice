package com.logistica.domain.liquidacion.models;

import com.logistica.domain.liquidacion.enums.EstadoPaquete;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class Paquete {

    private final UUID id;
    private final EstadoPaquete estadoFinal;
    private final String novedades;

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
