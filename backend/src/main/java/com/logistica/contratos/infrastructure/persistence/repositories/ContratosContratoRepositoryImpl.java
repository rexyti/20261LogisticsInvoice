package com.logistica.contratos.infrastructure.persistence.repositories;

import com.logistica.contratos.domain.exceptions.TransportistaNotFoundException;
import com.logistica.contratos.domain.models.ContratosContrato;
import com.logistica.contratos.domain.repositories.ContratosContratoRepository;
import com.logistica.contratos.infrastructure.adapters.ContratosContratoMapper;
import com.logistica.contratos.infrastructure.persistence.entities.ContratosContratoEntity;
import com.logistica.contratos.infrastructure.persistence.entities.ContratosTransportistaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ContratosContratoRepositoryImpl implements ContratosContratoRepository {

    private final ContratosContratoJpaRepository jpaRepository;
    private final ContratosTransportistaJpaRepository transportistaJpaRepository;
    private final ContratosContratoMapper mapper;

    @Override
    public ContratosContrato guardar(ContratosContrato contrato) {
        ContratosTransportistaEntity transportista = transportistaJpaRepository
                .findById(contrato.getTransportista().getTransportistaId())
                .orElseThrow(() -> new TransportistaNotFoundException(
                        contrato.getTransportista().getTransportistaId()));

        ContratosContratoEntity entity = mapper.toEntity(contrato, transportista);
        ContratosContratoEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<ContratosContrato> buscarPorIdContrato(String idContrato) {
        return jpaRepository.findByIdContrato(idContrato)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existePorIdContrato(String idContrato) {
        return jpaRepository.existsByIdContrato(idContrato);
    }

    @Override
    public Page<ContratosContrato> listar(Pageable pageable) {
        return jpaRepository.findAll(pageable)
                .map(mapper::toDomain);
    }
}
