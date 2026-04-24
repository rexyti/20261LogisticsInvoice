package com.logistica.infrastructure.persistence.repositories;

import com.logistica.domain.enums.TipoVehiculo;
import com.logistica.domain.repositories.TarifaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class TarifaRepositoryImpl implements TarifaRepository {

    @Override
    public boolean existeTarifaParaVehiculo(TipoVehiculo tipoVehiculo) {
        return tipoVehiculo != null;
    }
}
