package com.logistica.contratos.infrastructure.persistence.repositories;

import com.logistica.contratos.domain.models.Seguro;
import com.logistica.contratos.domain.repositories.SeguroRepository;
import com.logistica.contratos.infrastructure.persistence.entities.SeguroEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SeguroRepositoryImpl implements SeguroRepository {

    private final SeguroJpaRepository jpaRepository;

    @Override
    public Seguro guardar(Seguro seguro) {
        SeguroEntity entity = SeguroEntity.builder()
                .idSeguro(seguro.getIdSeguro())
                .numeroPoliza(seguro.getNumeroPoliza())
                .estado(seguro.getEstado())
                .build();
        SeguroEntity saved = jpaRepository.save(entity);
        return Seguro.builder()
                .idSeguro(saved.getIdSeguro())
                .numeroPoliza(saved.getNumeroPoliza())
                .estado(saved.getEstado())
                .build();
    }
}
