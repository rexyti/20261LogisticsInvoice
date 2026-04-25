package com.logistica.infrastructure.http.mappers;

import com.logistica.domain.enums.EstadoPaquete;
import com.logistica.infrastructure.http.dto.GestionPaqueteDTO;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GestionPaqueteMapper {

    public Optional<EstadoPaquete> mapearEstado(GestionPaqueteDTO dto) {
        if (dto == null) {
            return Optional.empty();
        }
        return EstadoPaquete.fromString(dto.estado());
    }
}
