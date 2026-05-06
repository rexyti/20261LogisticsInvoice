package com.logistica.application.visualizarLiquidacion.usecases.liquidacion;

import com.logistica.application.visualizarLiquidacion.dtos.response.VisualizarLiquidacionDetalleDTO;
import com.logistica.application.visualizarLiquidacion.mappers.VisualizarLiquidacionDTOMapper;
import com.logistica.application.visualizarLiquidacion.security.VisualizarLiquidacionUsuarioAutenticado;
import com.logistica.domain.visualizarLiquidacion.exceptions.LiquidacionNoEncontradaException;
import com.logistica.domain.visualizarLiquidacion.repositories.LiquidacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class VisualizarLiquidacionObtenerDetalleUseCase {

    private final LiquidacionRepository repository;
    private final VisualizarLiquidacionDTOMapper mapper;

    public VisualizarLiquidacionObtenerDetalleUseCase(LiquidacionRepository repository, VisualizarLiquidacionDTOMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public VisualizarLiquidacionDetalleDTO ejecutar(UUID id, VisualizarLiquidacionUsuarioAutenticado usuario) {
        var liquidacion = repository.buscarPorId(id)
                .orElseThrow(() -> new LiquidacionNoEncontradaException(
                        "La liquidacion con id " + id + " no existe en el registro."));

        usuario.verificarAcceso(liquidacion);

        return mapper.toDetalle(liquidacion);
    }
}
