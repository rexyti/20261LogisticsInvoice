package com.logistica.cierreRuta.application.mappers;

import com.logistica.cierreRuta.application.dtos.request.CierreRutaParadaEventDTO;
import com.logistica.cierreRuta.domain.enums.MotivoFalla;
import com.logistica.cierreRuta.domain.models.Parada;
import org.springframework.stereotype.Component;

@Component
public class CierreRutaParadaEventMapper {

    public Parada toDomain(CierreRutaParadaEventDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Parada no puede ser null");
        }

        if (dto.getParadaId() == null) {
            throw new IllegalArgumentException("paradaId es obligatorio");
        }

        MotivoFalla motivo = null;
        if (dto.getMotivoNoEntrega() != null && !dto.getMotivoNoEntrega().isBlank()) {
            motivo = MotivoFalla.fromValue(dto.getMotivoNoEntrega());
        }

        return Parada.builder()
                .paradaId(dto.getParadaId())
                .paqueteId(dto.getPaqueteId())
                .estado(dto.getEstado())
                .motivoFalla(motivo)
                .build();
    }
}
