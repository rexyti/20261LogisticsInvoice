package com.logistica.application.contratos.usecases.contrato;

import com.logistica.application.contratos.dtos.request.ContratoRequestDTO;
import com.logistica.application.contratos.dtos.response.ContratoResponseDTO;
import com.logistica.application.contratos.mappers.ContratoResponseMapper;
import com.logistica.domain.contratos.exceptions.ContratoYaExisteException;
import com.logistica.domain.contratos.exceptions.TransportistaNotFoundException;
import com.logistica.domain.contratos.models.Contrato;
import com.logistica.domain.contratos.models.Seguro;
import com.logistica.domain.contratos.models.Transportista;
import com.logistica.domain.contratos.repositories.ContratoRepository;
import com.logistica.domain.contratos.repositories.TransportistaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CrearContratoUseCase {

    private final ContratoRepository contratoRepository;
    private final TransportistaRepository transportistaRepository;
    private final ContratoResponseMapper responseMapper;

    @Transactional
    public ContratoResponseDTO ejecutar(ContratoRequestDTO dto) {

        // FR-004: idempotencia
        if (contratoRepository.existePorIdContrato(dto.getIdContrato())) {
            throw new ContratoYaExisteException(dto.getIdContrato());
        }

        // Busca el transportista existente — no lo crea
        Transportista transportista = transportistaRepository
                .buscarPorId(dto.getTransportistaId())
                .orElseThrow(() -> new TransportistaNotFoundException(
                        dto.getTransportistaId()));

        // Seguro como composición del agregado
        Seguro seguro = Seguro.builder()
                .idSeguro(UUID.randomUUID())
                .numeroPoliza(dto.getSeguro().getNumeroPoliza())
                .estado(dto.getSeguro().getEstado())
                .build();

        // Contrato.crear() valida fechas y precio condicional
        Contrato contrato = Contrato.crear(
                dto.getIdContrato(),
                dto.getTipoContrato(),
                transportista,
                dto.getTipoVehiculo(),
                dto.getEsPorParada(),
                dto.getPrecioParadas(),
                dto.getPrecio(),
                dto.getFechaInicio(),
                dto.getFechaFinal(),
                seguro
        );

        Contrato guardado = contratoRepository.guardar(contrato);
        return responseMapper.toResponseDTO(guardado);
    }
}