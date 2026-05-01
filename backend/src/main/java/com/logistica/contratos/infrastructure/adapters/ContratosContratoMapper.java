package com.logistica.contratos.infrastructure.adapters;

import com.logistica.contratos.domain.enums.ContratosTipoVehiculo;
import com.logistica.contratos.domain.models.ContratosContrato;
import com.logistica.contratos.domain.models.Seguro;
import com.logistica.contratos.domain.models.ContratosTransportista;
import com.logistica.contratos.infrastructure.persistence.entities.ContratosContratoEntity;
import com.logistica.contratos.infrastructure.persistence.entities.SeguroEntity;
import com.logistica.contratos.infrastructure.persistence.entities.ContratosTransportistaEntity;
import org.springframework.stereotype.Component;

@Component
public class ContratosContratoMapper {

    public ContratosContrato toDomain(ContratosContratoEntity entity) {
        if (entity == null) return null;

        return ContratosContrato.builder()
                .id(entity.getId())
                .idContrato(entity.getIdContrato())
                .tipoContrato(entity.getTipoContrato())
                .esPorParada(entity.getEsPorParada())
                .precioParadas(entity.getPrecioParadas())
                .precio(entity.getPrecio())
                .tipoVehiculo(ContratosTipoVehiculo.valueOf(entity.getTipoVehiculo()))
                .fechaInicio(entity.getFechaInicio())
                .fechaFinal(entity.getFechaFinal())
                .transportista(entity.getTransportista() != null ? ContratosTransportista.builder()
                        .transportistaId(entity.getTransportista().getIdTransportista())
                        .nombre(entity.getTransportista().getNombre())
                        .build() : null)
                .seguro(toSeguroDomain(entity.getSeguro()))
                .build();
    }

    public ContratosContratoEntity toEntity(ContratosContrato contrato, ContratosTransportistaEntity transportistaEntity) {
        if (contrato == null) return null;

        return ContratosContratoEntity.builder()
                .idContrato(contrato.getIdContrato())
                .tipoContrato(contrato.getTipoContrato())
                .esPorParada(contrato.getEsPorParada())
                .precioParadas(contrato.getPrecioParadas())
                .precio(contrato.getPrecio())
                .tipoVehiculo(contrato.getTipoVehiculo().name())
                .fechaInicio(contrato.getFechaInicio())
                .fechaFinal(contrato.getFechaFinal())
                .transportista(transportistaEntity)
                .seguro(toSeguroEntity(contrato.getSeguro()))
                .build();
    }

    private Seguro toSeguroDomain(SeguroEntity entity) {
        if (entity == null) return null;
        return Seguro.builder()
                .idSeguro(entity.getIdSeguro())
                .numeroPoliza(entity.getNumeroPoliza())
                .estado(entity.getEstado())
                .build();
    }

    private SeguroEntity toSeguroEntity(Seguro seguro) {
        if (seguro == null) return null;
        return SeguroEntity.builder()
                .numeroPoliza(seguro.getNumeroPoliza())
                .estado(seguro.getEstado())
                .build();
    }
}
