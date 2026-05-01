package com.logistica.contratos.application.usecases.contrato;

import com.logistica.contratos.application.dtos.request.ContratoRequestDTO;
import com.logistica.contratos.application.dtos.response.ContratoResponseDTO;
import com.logistica.contratos.application.mappers.ContratoResponseMapper;
import com.logistica.contratos.domain.exceptions.ContratoYaExisteException;
import com.logistica.contratos.domain.exceptions.TransportistaNotFoundException;
import com.logistica.contratos.domain.models.ContratosContrato;
import com.logistica.contratos.domain.models.Seguro;
import com.logistica.contratos.domain.models.ContratosTransportista;
import com.logistica.contratos.domain.repositories.ContratosContratoRepository;
import com.logistica.contratos.domain.repositories.ContratosTransportistaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CrearContratoUseCase {

    private final ContratosContratoRepository contratoRepository;
    private final ContratosTransportistaRepository transportistaRepository;
    private final ContratoResponseMapper responseMapper;

    @Transactional
    public ContratoResponseDTO ejecutar(ContratoRequestDTO dto) {

        // FR-004: idempotencia
        if (contratoRepository.existePorIdContrato(dto.getIdContrato())) {
            throw new ContratoYaExisteException(dto.getIdContrato());
        }

        // Busca el transportista existente — no lo crea
        ContratosTransportista transportista = transportistaRepository
                .buscarPorId(dto.getTransportistaId())
                .orElseThrow(() -> new TransportistaNotFoundException(
                        dto.getTransportistaId()));

        // Seguro como composición del agregado
        Seguro seguro = Seguro.builder()
                .idSeguro(UUID.randomUUID())
                .numeroPoliza(dto.getSeguro().getNumeroPoliza())
                .estado(dto.getSeguro().getEstado())
                .build();

        // ContratosContrato.crear() valida fechas y precio condicional
        ContratosContrato contrato = ContratosContrato.crear(
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

        ContratosContrato guardado = contratoRepository.guardar(contrato);
        return responseMapper.toResponseDTO(guardado);
    }
}