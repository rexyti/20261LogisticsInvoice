package com.logistica.contratos.application.usecases.contrato;

import com.logistica.contratos.application.dtos.request.ContratoRequestDTO;
import com.logistica.contratos.application.dtos.response.ContratoResponseDTO;
import com.logistica.contratos.application.mappers.ContratoResponseMapper;
import com.logistica.contratos.domain.exceptions.ContratoYaExisteException;
import com.logistica.contratos.domain.exceptions.TransportistaNotFoundException;
import com.logistica.contratos.domain.models.Contrato;
import com.logistica.contratos.domain.models.Seguro;
import com.logistica.contratos.domain.models.Transportista;
import com.logistica.contratos.domain.repositories.ContratoRepository;
import com.logistica.contratos.domain.repositories.TransportistaRepository;
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