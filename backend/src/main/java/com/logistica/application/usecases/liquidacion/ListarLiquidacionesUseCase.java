package com.logistica.application.usecases.liquidacion;

import com.logistica.application.dtos.response.LiquidacionListItemDTO;
import com.logistica.application.dtos.response.LiquidacionListResponseDTO;
import com.logistica.application.mappers.LiquidacionDTOMapper;
import com.logistica.application.security.UsuarioAutenticado;
import com.logistica.domain.models.Liquidacion;
import com.logistica.domain.repositories.LiquidacionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ListarLiquidacionesUseCase {

    private final LiquidacionRepository repository;
    private final LiquidacionDTOMapper mapper;

    public ListarLiquidacionesUseCase(LiquidacionRepository repository, LiquidacionDTOMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public LiquidacionListResponseDTO ejecutar(Pageable pageable, UsuarioAutenticado usuario) {
        Page<Liquidacion> pagina = usuario.tienePermisoGlobal()
                ? repository.listarTodas(pageable)
                : repository.listarPorUsuario(usuario.getUsuarioId(), pageable);

        List<LiquidacionListItemDTO> contenido = pagina.getContent().stream()
                .map(mapper::toListItem)
                .toList();

        return new LiquidacionListResponseDTO(
                contenido,
                pagina.getNumber(),
                pagina.getSize(),
                pagina.getTotalElements(),
                pagina.getTotalPages(),
                pagina.isLast()
        );
    }
}
