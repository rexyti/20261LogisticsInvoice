package com.logistica.application.visualizarEstadoPago.usecases.pago;

import com.logistica.application.visualizarEstadoPago.dtos.response.PagoListDTO;
import com.logistica.application.visualizarEstadoPago.mappers.PagoDtoMapper;
import com.logistica.domain.visualizarEstadoPago.repositories.VisualizarEstadoPagoPagoRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ListarPagosUseCase {

    private final VisualizarEstadoPagoPagoRepository pagoRepository;
    private final PagoDtoMapper pagoDtoMapper;

    public ListarPagosUseCase(VisualizarEstadoPagoPagoRepository pagoRepository) {
        this.pagoRepository = pagoRepository;
        this.pagoDtoMapper = new PagoDtoMapper();
    }

    public List<PagoListDTO> ejecutar(UUID usuarioId) {
        return pagoRepository.findByUsuarioId(usuarioId).stream()
                .map(pagoDtoMapper::toPagoListDTO)
                .collect(Collectors.toList());
    }
}
