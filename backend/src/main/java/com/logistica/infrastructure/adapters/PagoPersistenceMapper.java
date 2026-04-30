package com.logistica.infrastructure.adapters;

import com.logistica.domain.models.Pago;
import com.logistica.infrastructure.persistence.entities.PagoEntity;
import org.springframework.stereotype.Component;

@Component
public class PagoPersistenceMapper {

    public Pago toDomain(PagoEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Pago(
                entity.getId(),
                entity.getUsuarioId(),
                entity.getMontoBase(),
                entity.getFecha(),
                entity.getPenalidadId(),
                entity.getMontoNeto(),
                entity.getLiquidacionId(),
                entity.getEstado()
        );
    }

    public PagoEntity toEntity(Pago domain) {
        if (domain == null) {
            return null;
        }
        return new PagoEntity(
                domain.getId(),
                domain.getUsuarioId(),
                domain.getMontoBase(),
                domain.getFecha(),
                domain.getPenalidadId(),
                domain.getMontoNeto(),
                domain.getLiquidacionId(),
                domain.getEstado()
        );
    }
}
