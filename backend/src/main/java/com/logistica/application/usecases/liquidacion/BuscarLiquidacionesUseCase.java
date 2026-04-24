package com.logistica.application.usecases.liquidacion;

import com.logistica.application.dtos.request.FiltroLiquidacionDTO;
import com.logistica.application.dtos.response.LiquidacionDetalleDTO;
import com.logistica.application.mappers.LiquidacionDTOMapper;
import com.logistica.application.security.UsuarioAutenticado;
import com.logistica.domain.exceptions.LiquidacionAunNoCalculadaException;
import com.logistica.domain.exceptions.LiquidacionNoEncontradaException;
import com.logistica.domain.models.Liquidacion;
import com.logistica.domain.models.ResultadoBusquedaPorRuta;
import com.logistica.domain.repositories.LiquidacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class BuscarLiquidacionesUseCase {

    private final LiquidacionRepository repository;
    private final LiquidacionDTOMapper mapper;

    public BuscarLiquidacionesUseCase(LiquidacionRepository repository, LiquidacionDTOMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public LiquidacionDetalleDTO ejecutar(FiltroLiquidacionDTO filtro, UsuarioAutenticado usuario) {
        Liquidacion liquidacion = resolverLiquidacion(filtro);
        usuario.verificarAcceso(liquidacion);
        return mapper.toDetalle(liquidacion);
    }

    private Liquidacion resolverLiquidacion(FiltroLiquidacionDTO filtro) {
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
