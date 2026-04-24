package com.logistica.application.mappers;

import com.logistica.application.dtos.request.ParadaEventDTO;
import com.logistica.domain.enums.EstadoParada;
import com.logistica.domain.enums.MotivoFalla;
import com.logistica.domain.models.Parada;
import org.springframework.stereotype.Component;

@Component
public class ParadaEventMapper {

    public Parada toDomain(ParadaEventDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Parada no puede ser null");
        }

        if (dto.getParadaId() == null) {
            throw new IllegalArgumentException("paradaId es obligatorio");
        }

        EstadoParada estado;
        try {
            estado = EstadoParada.valueOf(String.valueOf(dto.getEstado()));
        } catch (Exception e) {
            throw new IllegalArgumentException("Estado inválido: " + dto.getEstado());
        }

        MotivoFalla motivo = null;

        if (dto.getMotivoNoEntrega() != null) {
            motivo = MotivoFalla.fromValue(dto.getMotivoNoEntrega());
        }


        return Parada.crear(
                dto.getParadaId(),
                estado,
                motivo
        );
    }
}