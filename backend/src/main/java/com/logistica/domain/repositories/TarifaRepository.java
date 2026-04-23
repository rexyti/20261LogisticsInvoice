package com.logistica.domain.repositories;

import com.logistica.domain.enums.TipoVehiculo;

public interface TarifaRepository {
    boolean existeTarifaParaVehiculo(TipoVehiculo tipoVehiculo);
}
