package com.logistica.contratos.infrastructure.persistence.repositories;

import com.logistica.contratos.domain.exceptions.TransportistaNotFoundException;
import com.logistica.contratos.domain.models.Contrato;
import com.logistica.contratos.domain.repositories.ContratoRepository;
import com.logistica.contratos.infrastructure.adapters.ContratoMapper;
import com.logistica.contratos.infrastructure.persistence.entities.ContratoEntity;
import com.logistica.contratos.infrastructure.persistence.entities.TransportistaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ContratoRepositoryImpl implements ContratoRepository {

    private final ContratoJpaRepository jpaRepository;
    private final TransportistaJpaRepository transportistaJpaRepository;
    private final ContratoMapper mapper;

    @Override
    public Contrato guardar(Contrato contrato) {
        TransportistaEntity transportista = transportistaJpaRepository
                .findById(contrato.getTransportista().getTransportistaId())
                .orElseThrow(() -> new TransportistaNotFoundException(
                        contrato.getTransportista().getTransportistaId()));

        ContratoEntity entity = mapper.toEntity(contrato, transportista);
        ContratoEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Contrato> buscarPorIdContrato(String idContrato) {
        return jpaRepository.findByIdContrato(idContrato)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existePorIdContrato(String idContrato) {
        return jpaRepository.existsByIdContrato(idContrato);
    }

    @Override
    public Page<Contrato> listar(Pageable pageable) {
        return jpaRepository.findAll(pageable)
                .map(mapper::toDomain);
    }
}
