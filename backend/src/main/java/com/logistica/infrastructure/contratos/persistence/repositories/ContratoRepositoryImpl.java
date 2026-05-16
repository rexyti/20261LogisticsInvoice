package com.logistica.infrastructure.contratos.persistence.repositories;

import com.logistica.domain.contratos.exceptions.RecursoNoEncontradoException;
import com.logistica.domain.contratos.models.Contrato;
import com.logistica.domain.contratos.repositories.ContratoRepository;
import com.logistica.infrastructure.contratos.adapters.ContratoMapper;
import com.logistica.infrastructure.contratos.persistence.entities.ContratoEntity;
import com.logistica.infrastructure.contratos.persistence.entities.TransportistaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ContratoRepositoryImpl implements ContratoRepository {

    private final ContratoJpaRepository jpaRepository;
    private final ContratoTransportistaJpaRepository transportistaJpaRepository;
    private final ContratoMapper mapper;

    @Override
    @Transactional
    public Contrato guardar(Contrato contrato) {
        TransportistaEntity transportista = transportistaJpaRepository
                .findById(contrato.getTransportista().getTransportistaId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Transportista no encontrado: " + contrato.getTransportista().getTransportistaId()));

        ContratoEntity entity = mapper.toEntity(contrato, transportista);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public boolean existePorIdContrato(String idContrato) {
        return jpaRepository.existsByIdContrato(idContrato);
    }

    @Override
    public Optional<Contrato> buscarPorIdContrato(String idContrato) {
        return jpaRepository.findByIdContrato(idContrato).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Contrato> listar(Pageable pageable) {
        return jpaRepository.findAll(pageable).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Contrato> listarPorTransportista(UUID idTransportista, Pageable pageable) {
        return jpaRepository.findAllByTransportista_Id(idTransportista, pageable).map(mapper::toDomain);
    }
}
