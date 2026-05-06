package com.logistica.application.cierreRuta.usecases.ruta;

import com.logistica.application.cierreRuta.dtos.response.RutaProcesadaResponseDTO;
import com.logistica.application.cierreRuta.mappers.RutaResponseMapper;
import com.logistica.domain.cierreRuta.enums.EstadoProcesamiento;
import com.logistica.domain.cierreRuta.exceptions.RutaNotFoundException;
import com.logistica.domain.cierreRuta.models.RutaCerrada;
import com.logistica.domain.cierreRuta.repositories.RutaRepository;
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
        RutaCerrada ruta = rutaRepository.buscarPorRutaId(rutaId)
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
