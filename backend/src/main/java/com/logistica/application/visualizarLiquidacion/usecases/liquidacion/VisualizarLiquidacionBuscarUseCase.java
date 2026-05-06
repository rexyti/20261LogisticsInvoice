package com.logistica.application.visualizarLiquidacion.usecases.liquidacion;

import com.logistica.application.visualizarLiquidacion.dtos.request.VisualizarLiquidacionFiltroDTO;
import com.logistica.application.visualizarLiquidacion.dtos.response.VisualizarLiquidacionDetalleDTO;
import com.logistica.application.visualizarLiquidacion.mappers.VisualizarLiquidacionDTOMapper;
import com.logistica.application.visualizarLiquidacion.security.VisualizarLiquidacionUsuarioAutenticado;
import com.logistica.domain.visualizarLiquidacion.exceptions.LiquidacionAunNoCalculadaException;
import com.logistica.domain.visualizarLiquidacion.exceptions.LiquidacionNoEncontradaException;
import com.logistica.domain.visualizarLiquidacion.models.Liquidacion;
import com.logistica.domain.visualizarLiquidacion.models.ResultadoBusquedaPorRuta;
import com.logistica.domain.visualizarLiquidacion.repositories.LiquidacionRepository;
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
