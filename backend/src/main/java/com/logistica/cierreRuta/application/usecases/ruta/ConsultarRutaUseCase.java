package com.logistica.cierreRuta.application.usecases.ruta;

import com.logistica.cierreRuta.application.dtos.response.RutaProcesadaResponseDTO;
import com.logistica.cierreRuta.application.mappers.RutaResponseMapper;
import com.logistica.cierreRuta.domain.enums.EstadoProcesamiento;
import com.logistica.cierreRuta.domain.exceptions.RutaNotFoundException;
import com.logistica.cierreRuta.domain.models.CierreRutaRuta;
import com.logistica.cierreRuta.domain.repositories.RutaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConsultarRutaUseCase {

    private final RutaRepository rutaRepository;
    private final RutaResponseMapper rutaResponseMapper;

    public RutaProcesadaResponseDTO ejecutar(UUID rutaId) {
        CierreRutaRuta ruta = rutaRepository.buscarPorRutaId(rutaId)
                .orElseThrow(() -> new RutaNotFoundException(rutaId));

        return rutaResponseMapper.toResponse(ruta);
    }

    public Page<RutaProcesadaResponseDTO> listarTodas(EstadoProcesamiento estado, Pageable pageable) {
        if (estado != null) {
            return rutaRepository.buscarPorEstado(estado, pageable)
                    .map(rutaResponseMapper::toResponse);
        }
        return rutaRepository.listarTodas(pageable)
                .map(rutaResponseMapper::toResponse);
    }
}
