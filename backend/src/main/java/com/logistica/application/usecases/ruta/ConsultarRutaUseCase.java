package com.logistica.application.usecases.ruta;

import com.logistica.application.dtos.response.ParadaResponseDTO;
import com.logistica.application.dtos.response.RutaProcesadaResponseDTO;
import com.logistica.application.dtos.response.TransportistaResponseDTO;
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
public class ConsultarRutaUseCase {

    private final RutaRepository rutaRepository;

    @Transactional(readOnly = true)
    public RutaProcesadaResponseDTO ejecutar(UUID rutaId) {
        Ruta ruta = rutaRepository.buscarPorRutaId(rutaId)
                .orElseThrow(() -> new RutaNotFoundException(rutaId));
        return toResponseDTO(ruta);
    }

    @Transactional(readOnly = true)
    public List<RutaProcesadaResponseDTO> listarTodas() {
        return rutaRepository.listarTodas().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private RutaProcesadaResponseDTO toResponseDTO(Ruta ruta) {
        return RutaProcesadaResponseDTO.builder()
                .rutaId(ruta.getRutaId())
                .tipoVehiculo(ruta.getTipoVehiculo())
                .modeloContrato(ruta.getModeloContrato())
                .estadoProcesamiento(ruta.getEstadoProcesamiento().name())
                .fechaInicioTransito(ruta.getFechaInicioTransito())
                .fechaCierre(ruta.getFechaCierre())
                .transportista(TransportistaResponseDTO.builder()
                        .conductorId(ruta.getTransportista().getConductorId())
                        .nombre(ruta.getTransportista().getNombre())
                        .build())
                .paradas(ruta.getParadas().stream()
                        .map(p -> ParadaResponseDTO.builder()
                                .paradaId(p.getParadaId())
                                .estado(p.getEstado().name())
                                .motivoFalla(p.getMotivoFalla() != null ? p.getMotivoFalla().name() : null)
                                .responsable(p.getResponsable() != null ? p.getResponsable().name() : null)
                                .build())
                        .toList())
                .build();
    }
}
