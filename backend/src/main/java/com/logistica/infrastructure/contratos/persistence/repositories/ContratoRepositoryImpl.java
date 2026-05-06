package com.logistica.infrastructure.contratos.persistence.repositories;

import com.logistica.domain.contratos.exceptions.TransportistaNotFoundException;
import com.logistica.domain.contratos.models.Contrato;
import com.logistica.domain.contratos.repositories.ContratoRepository;
import com.logistica.infrastructure.contratos.adapters.ContratoMapper;
import com.logistica.infrastructure.contratos.persistence.entities.ContratoEntity;
import com.logistica.infrastructure.contratos.persistence.entities.TransportistaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ContratoRepositoryImpl implements ContratoRepository {

    private final ContratoJpaRepository jpaRepository;
    private final ContratoTransportistaJpaRepository transportistaJpaRepository;
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
