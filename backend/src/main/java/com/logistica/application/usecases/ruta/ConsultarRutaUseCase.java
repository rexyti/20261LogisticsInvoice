package com.logistica.application.usecases.ruta;

import com.logistica.application.dtos.response.RutaProcesadaResponseDTO;
import com.logistica.application.mappers.RutaResponseMapper;
import com.logistica.domain.enums.EstadoProcesamiento;
import com.logistica.domain.exceptions.RutaNotFoundException;
import com.logistica.domain.models.Ruta;
import com.logistica.domain.repositories.RutaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConsultarRutaUseCase {

    private final RutaRepository rutaRepository;
    private final RutaResponseMapper rutaResponseMapper;

    public RutaProcesadaResponseDTO ejecutar(UUID rutaId) {
        Ruta ruta = rutaRepository.buscarPorRutaId(rutaId)
                .orElseThrow(() -> new RutaNotFoundException(rutaId));

        return rutaResponseMapper.toResponse(ruta);
    }

    public List<RutaProcesadaResponseDTO> listarTodas() {
        return rutaRepository.listarTodas().stream()
                .map(rutaResponseMapper::toResponse)
                .toList();
    }

    public Page<RutaProcesadaResponseDTO> listarTodas(EstadoProcesamiento estado, Pageable pageable) {
        // Por ahora, como el repositorio no soporta paginación ni filtros,
        // implementamos una solución temporal que mantenga la firma del controlador.
        // TODO: Actualizar RutaRepository para soportar filtrado y paginación en DB.
        List<RutaProcesadaResponseDTO> todas = listarTodas();
        
        if (estado != null) {
            todas = todas.stream()
                    .filter(r -> r.getEstadoProcesamiento().equals(estado.name()))
                    .toList();
        }

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), todas.size());
        
        if (start > todas.size()) {
            return new PageImpl<>(List.of(), pageable, todas.size());
        }

        return new PageImpl<>(todas.subList(start, end), pageable, todas.size());
    }
}
