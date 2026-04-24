package com.logistica.application.usecases.liquidacion;

import com.logistica.application.dtos.response.LiquidacionDetalleDTO;
import com.logistica.application.mappers.LiquidacionDTOMapper;
import com.logistica.application.security.UsuarioAutenticado;
import com.logistica.domain.exceptions.LiquidacionNoEncontradaException;
import com.logistica.domain.repositories.LiquidacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ObtenerDetalleLiquidacionUseCase {

    private final LiquidacionRepository repository;
    private final LiquidacionDTOMapper mapper;

    public ObtenerDetalleLiquidacionUseCase(LiquidacionRepository repository, LiquidacionDTOMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public LiquidacionDetalleDTO ejecutar(UUID id, UsuarioAutenticado usuario) {
        var liquidacion = repository.buscarPorId(id)
                .orElseThrow(() -> new LiquidacionNoEncontradaException(
                        "La liquidacion con id " + id + " no existe en el registro."));

        usuario.verificarAcceso(liquidacion);

        return mapper.toDetalle(liquidacion);
    }
}
