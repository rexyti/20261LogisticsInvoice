package com.logistica.application.visualizarLiquidacion.usecases.liquidacion;

import com.logistica.application.visualizarLiquidacion.dtos.response.VisualizarLiquidacionListItemDTO;
import com.logistica.application.visualizarLiquidacion.dtos.response.VisualizarLiquidacionListResponseDTO;
import com.logistica.application.visualizarLiquidacion.mappers.VisualizarLiquidacionDTOMapper;
import com.logistica.application.visualizarLiquidacion.security.VisualizarLiquidacionUsuarioAutenticado;
import com.logistica.domain.visualizarLiquidacion.models.Liquidacion;
import com.logistica.domain.visualizarLiquidacion.repositories.LiquidacionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class VisualizarLiquidacionListarUseCase {

    private final LiquidacionRepository repository;
    private final VisualizarLiquidacionDTOMapper mapper;

    public VisualizarLiquidacionListarUseCase(LiquidacionRepository repository, VisualizarLiquidacionDTOMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public VisualizarLiquidacionListResponseDTO ejecutar(Pageable pageable, VisualizarLiquidacionUsuarioAutenticado usuario) {
        Page<Liquidacion> pagina = usuario.tienePermisoGlobal()
                ? repository.listarTodas(pageable)
                : repository.listarPorUsuario(usuario.getUsuarioId(), pageable);

        List<VisualizarLiquidacionListItemDTO> contenido = pagina.getContent().stream()
                .map(mapper::toListItem)
                .toList();

        return new VisualizarLiquidacionListResponseDTO(
                contenido,
                pagina.getNumber(),
                pagina.getSize(),
                pagina.getTotalElements(),
                pagina.getTotalPages(),
                pagina.isLast()
        );
    }
}
