package com.logistica.domain.liquidacion.strategies;

import com.logistica.domain.liquidacion.enums.TipoContratacion;
import com.logistica.domain.liquidacion.models.ContratoTarifa;
import com.logistica.domain.liquidacion.models.RutaLiquidacion;

import java.math.BigDecimal;

public interface Strategy {

    TipoContratacion soporta();

    BigDecimal calcular(RutaLiquidacion ruta, ContratoTarifa contrato);
}