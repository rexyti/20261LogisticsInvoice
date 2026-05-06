package com.logistica.domain.liquidacion.strategies;

import com.logistica.domain.liquidacion.enums.TipoContratacion;
import com.logistica.domain.liquidacion.models.ContratoTarifa;
import com.logistica.domain.liquidacion.models.RutaLiquidacion;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class RecorridoCompletoStrategy implements Strategy {

    @Override
    public TipoContratacion soporta() {
        return TipoContratacion.RECORRIDO_COMPLETO;
    }

    @Override
    public BigDecimal calcular(RutaLiquidacion ruta, ContratoTarifa contrato) {

        validarContrato(contrato);
        validarRuta(ruta);

        if (!ruta.fueCompletada()) {
            return BigDecimal.ZERO;
        }

        return contrato.getTarifaSegura();
    }

    private void validarRuta(RutaLiquidacion ruta) {
        if (ruta == null) {
            throw new IllegalArgumentException("La ruta no puede ser null");
        }
    }

    private void validarContrato(ContratoTarifa contrato) {
        if (contrato == null || !contrato.esRecorridoCompleto()) {
            throw new IllegalArgumentException("El contrato no es de tipo RECORRIDO_COMPLETO");
        }
    }
}