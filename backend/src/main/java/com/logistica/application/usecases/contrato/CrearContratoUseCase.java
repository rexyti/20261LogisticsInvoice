package com.logistica.application.usecases.contrato;

import com.logistica.application.dtos.request.ContratoRequestDTO;
import com.logistica.application.dtos.response.ContratoResponseDTO;
import com.logistica.domain.exceptions.ContratoYaExisteException;
import com.logistica.domain.models.Contrato;
import com.logistica.domain.models.Seguro;
import com.logistica.domain.models.Usuario;
import com.logistica.domain.models.Vehiculo;
import com.logistica.domain.repositories.ContratoRepository;
import com.logistica.domain.repositories.SeguroRepository;
import com.logistica.domain.repositories.UsuarioRepository;
import com.logistica.domain.repositories.VehiculoRepository;
import com.logistica.infrastructure.adapters.ContratoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CrearContratoUseCase {

    private final ContratoRepository contratoRepository;
    private final UsuarioRepository usuarioRepository;
    private final VehiculoRepository vehiculoRepository;
    private final SeguroRepository seguroRepository;
    private final ContratoMapper contratoMapper;

    @Transactional
    public ContratoResponseDTO ejecutar(ContratoRequestDTO dto) {
        if (contratoRepository.existePorIdContrato(dto.getIdContrato())) {
            throw new ContratoYaExisteException(dto.getIdContrato());
        }

        Usuario usuario = usuarioRepository.guardar(
                Usuario.builder().nombre(dto.getNombreConductor()).build()
        );

        Vehiculo vehiculo = vehiculoRepository.guardar(
                Vehiculo.builder().idUsuario(usuario.getIdUsuario()).tipo(dto.getTipoVehiculo()).build()
        );

        seguroRepository.guardar(
                Seguro.builder().idUsuario(usuario.getIdUsuario()).estado(dto.getEstadoSeguro()).build()
        );

        Contrato contrato = contratoMapper.toDomain(dto, usuario.getIdUsuario(), vehiculo.getIdVehiculo());
        Contrato guardado = contratoRepository.guardar(contrato);

        return contratoMapper.toResponseDTO(guardado);
    }
}
