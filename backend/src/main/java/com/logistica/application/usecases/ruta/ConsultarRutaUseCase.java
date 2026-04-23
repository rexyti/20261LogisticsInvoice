package com.logistica.application.usecases.ruta;

import com.logistica.application.dtos.response.ParadaResponseDTO;
import com.logistica.application.dtos.response.RutaProcesadaResponseDTO;
import com.logistica.application.dtos.response.TransportistaResponseDTO;
import com.logistica.application.mappers.RutaResponseMapper;
import com.logistica.domain.exceptions.RutaNotFoundException;
import com.logistica.domain.models.Ruta;
import com.logistica.domain.repositories.RutaRepository;
import lombok.RequiredArgsConstructor;
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
}
