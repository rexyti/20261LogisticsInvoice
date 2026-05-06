package com.logistica.infrastructure.contratos.adapters;

import com.logistica.domain.shared.enums.TipoVehiculo;
import com.logistica.domain.contratos.models.Contrato;
import com.logistica.domain.contratos.models.Seguro;
import com.logistica.domain.contratos.models.Transportista;
import com.logistica.infrastructure.contratos.persistence.entities.ContratoEntity;
import com.logistica.infrastructure.contratos.persistence.entities.SeguroEntity;
import com.logistica.infrastructure.contratos.persistence.entities.TransportistaEntity;
import org.springframework.stereotype.Component;

@Component
public class ContratoMapper {

    public Contrato toDomain(ContratoEntity entity) {
        if (entity == null) return null;

        return Contrato.builder()
                .id(entity.getId())
                .idContrato(entity.getIdContrato())
                .tipoContrato(entity.getTipoContrato())
                .esPorParada(entity.getEsPorParada())
                .precioParadas(entity.getPrecioParadas())
                .precio(entity.getPrecio())
                .tipoVehiculo(TipoVehiculo.valueOf(entity.getTipoVehiculo()))
                .fechaInicio(entity.getFechaInicio())
                .fechaFinal(entity.getFechaFinal())
                .transportista(entity.getTransportista() != null ? Transportista.builder()
                        .transportistaId(entity.getTransportista().getIdTransportista())
                        .nombre(entity.getTransportista().getNombre())
                        .build() : null)
                .seguro(toSeguroDomain(entity.getSeguro()))
                .build();
    }

    public ContratoEntity toEntity(Contrato contrato, TransportistaEntity transportistaEntity) {
        if (contrato == null) return null;

        return ContratoEntity.builder()
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
