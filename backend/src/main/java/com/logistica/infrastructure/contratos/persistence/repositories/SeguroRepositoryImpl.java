package com.logistica.infrastructure.contratos.persistence.repositories;

import com.logistica.domain.contratos.models.Seguro;
import com.logistica.domain.contratos.repositories.SeguroRepository;
import com.logistica.infrastructure.contratos.persistence.entities.SeguroEntity;
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
