package com.logistica.VisualizarLiquidación.application.usecases.liquidacion;

import com.logistica.VisualizarLiquidación.application.dtos.request.VisualizarLiquidacionFiltroDTO;
import com.logistica.VisualizarLiquidación.application.dtos.response.VisualizarLiquidacionDetalleDTO;
import com.logistica.VisualizarLiquidación.application.mappers.VisualizarLiquidacionDTOMapper;
import com.logistica.VisualizarLiquidación.application.security.VisualizarLiquidacionUsuarioAutenticado;
import com.logistica.VisualizarLiquidación.domain.exceptions.LiquidacionAunNoCalculadaException;
import com.logistica.VisualizarLiquidación.domain.exceptions.LiquidacionNoEncontradaException;
import com.logistica.VisualizarLiquidación.domain.models.Liquidacion;
import com.logistica.VisualizarLiquidación.domain.models.ResultadoBusquedaPorRuta;
import com.logistica.VisualizarLiquidación.domain.repositories.LiquidacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class VisualizarLiquidacionBuscarUseCase {

    private final LiquidacionRepository repository;
    private final VisualizarLiquidacionDTOMapper mapper;

    public VisualizarLiquidacionBuscarUseCase(LiquidacionRepository repository, VisualizarLiquidacionDTOMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public VisualizarLiquidacionDetalleDTO ejecutar(VisualizarLiquidacionFiltroDTO filtro, VisualizarLiquidacionUsuarioAutenticado usuario) {
        Liquidacion liquidacion = resolverLiquidacion(filtro);
        usuario.verificarAcceso(liquidacion);
        return mapper.toDetalle(liquidacion);
    }

    private Liquidacion resolverLiquidacion(VisualizarLiquidacionFiltroDTO filtro) {
        if (filtro.getIdLiquidacion() != null) {
            return repository.buscarPorId(filtro.getIdLiquidacion())
                    .orElseThrow(() -> new LiquidacionNoEncontradaException(
                            "La liquidacion con id " + filtro.getIdLiquidacion() + " no existe en el registro."));
        }

        return switch (repository.buscarPorIdRuta(filtro.getIdRuta())) {
            case ResultadoBusquedaPorRuta.Encontrada e -> e.liquidacion();
            case ResultadoBusquedaPorRuta.RutaSinLiquidacion ignored ->
                    throw new LiquidacionAunNoCalculadaException(
                            "La ruta con id " + filtro.getIdRuta() + " aun no posee una liquidacion calculada.");
            case ResultadoBusquedaPorRuta.RutaNoExiste ignored ->
                    throw new LiquidacionNoEncontradaException(
                            "No existe ninguna liquidacion asociada al id de ruta " + filtro.getIdRuta() + ".");
        };
    }
}
