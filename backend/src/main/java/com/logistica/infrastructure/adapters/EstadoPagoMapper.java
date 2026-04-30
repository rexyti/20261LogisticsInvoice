package com.logistica.infrastructure.adapters;

import com.logistica.domain.models.EstadoPago;
import com.logistica.infrastructure.persistence.entities.EstadoPagoEntity;
import org.springframework.stereotype.Component;

@Component
public class EstadoPagoMapper {

    public EstadoPago toDomain(EstadoPagoEntity entity) {
        if (entity == null) {
            return null;
        }
        return new EstadoPago(
                entity.getId(),
                entity.getPagoId(),
                entity.getEstado()
        );
    }

    public EstadoPagoEntity toEntity(EstadoPago domain) {
        if (domain == null) {
            return null;
        }
        return new EstadoPagoEntity(
                domain.getId(),
                domain.getPagoId(),
                domain.getEstado()
        );
    }
}
