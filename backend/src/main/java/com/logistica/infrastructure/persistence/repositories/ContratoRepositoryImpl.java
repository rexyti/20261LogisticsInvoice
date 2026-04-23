package com.logistica.infrastructure.persistence.repositories;

import com.logistica.domain.models.Contrato;
import com.logistica.domain.repositories.ContratoRepository;
import com.logistica.infrastructure.adapters.ContratoMapper;
import com.logistica.infrastructure.persistence.entities.ContratoEntity;
import com.logistica.infrastructure.persistence.entities.UsuarioEntity;
import com.logistica.infrastructure.persistence.entities.VehiculoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ContratoRepositoryImpl implements ContratoRepository {

    private final ContratoJpaRepository jpaRepository;
    private final UsuarioJpaRepository usuarioJpaRepository;
    private final VehiculoJpaRepository vehiculoJpaRepository;
    private final ContratoMapper mapper;

    @Override
    public Contrato guardar(Contrato contrato) {
        UsuarioEntity usuario = usuarioJpaRepository.getReferenceById(contrato.getIdUsuario());
        VehiculoEntity vehiculo = vehiculoJpaRepository.getReferenceById(contrato.getIdVehiculo());

        ContratoEntity entity = mapper.toEntity(contrato, usuario, vehiculo);
        ContratoEntity saved = jpaRepository.save(entity);
        return mapper.toDomainFromEntity(saved);
    }

    @Override
    public Optional<Contrato> buscarPorIdContrato(String idContrato) {
        return jpaRepository.findByIdContrato(idContrato)
                .map(mapper::toDomainFromEntity);
    }

    @Override
    public boolean existePorIdContrato(String idContrato) {
        return jpaRepository.existsByIdContrato(idContrato);
    }

    @Override
    public List<Contrato> listar() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomainFromEntity)
                .toList();
    }
}
