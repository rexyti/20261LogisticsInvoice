package com.logistica.liquidacion.domain.strategies;

import com.logistica.liquidacion.domain.enums.TipoContratacion;
import com.logistica.liquidacion.domain.models.Contrato;
import com.logistica.liquidacion.domain.models.Ruta;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class RecorridoCompletoStrategy implements LiquidacionStrategy {

    @Override
    public TipoContratacion soporta() {
        return TipoContratacion.RECORRIDO_COMPLETO;
    }

    @Override
    public BigDecimal calcular(Ruta ruta, Contrato contrato) {

        validarContrato(contrato);
        validarRuta(ruta);

        if (!ruta.fueCompletada()) {
            return BigDecimal.ZERO;
        }

        return contrato.getTarifaSegura();
    }

    private void validarRuta(Ruta ruta) {
        if (ruta == null) {
            throw new IllegalArgumentException("La ruta no puede ser null");
        }
    }

    private void validarContrato(Contrato contrato) {
        if (contrato == null || !contrato.esRecorridoCompleto()) {
            throw new IllegalArgumentException("El contrato no es de tipo RECORRIDO_COMPLETO");
        }
    }
}