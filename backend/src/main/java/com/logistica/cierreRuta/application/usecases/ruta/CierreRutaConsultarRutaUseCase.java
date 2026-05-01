package com.logistica.cierreRuta.application.usecases.ruta;

import com.logistica.cierreRuta.application.dtos.response.CierreRutaRutaProcesadaResponseDTO;
import com.logistica.cierreRuta.application.mappers.CierreRutaRutaResponseMapper;
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
public class CierreRutaConsultarRutaUseCase {

    private final RutaRepository rutaRepository;
    private final CierreRutaRutaResponseMapper rutaResponseMapper;

    public CierreRutaRutaProcesadaResponseDTO ejecutar(UUID rutaId) {
        CierreRutaRuta ruta = rutaRepository.buscarPorRutaId(rutaId)
                .orElseThrow(() -> new RutaNotFoundException(rutaId));

        return rutaResponseMapper.toResponse(ruta);
    }

    public Page<CierreRutaRutaProcesadaResponseDTO> listarTodas(EstadoProcesamiento estado, Pageable pageable) {
        if (estado != null) {
            return rutaRepository.buscarPorEstado(estado, pageable)
                    .map(rutaResponseMapper::toResponse);
        }
        return rutaRepository.listarTodas(pageable)
                .map(rutaResponseMapper::toResponse);
    }
}
