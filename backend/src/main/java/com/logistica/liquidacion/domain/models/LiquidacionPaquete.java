package com.logistica.liquidacion.domain.models;

import com.logistica.liquidacion.domain.enums.LiquidacionEstadoPaquete;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class LiquidacionPaquete {

    private final UUID id;
    private final LiquidacionEstadoPaquete estadoFinal;
    private final String novedades;

    public boolean esEntregado() {
        return estadoFinal == LiquidacionEstadoPaquete.ENTREGADO;
    }

    public boolean esFallidoCliente() {
        return estadoFinal == LiquidacionEstadoPaquete.FALLIDO_CLIENTE;
    }

    public boolean esFallidoTransportista() {
        return estadoFinal == LiquidacionEstadoPaquete.FALLIDO_TRANSPORTISTA;
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
