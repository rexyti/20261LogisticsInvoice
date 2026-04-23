package com.logistica.infrastructure.persistence.repositories;

import com.logistica.domain.models.Seguro;
import com.logistica.domain.repositories.SeguroRepository;
import com.logistica.infrastructure.persistence.entities.SeguroEntity;
import com.logistica.infrastructure.persistence.entities.UsuarioEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SeguroRepositoryImpl implements SeguroRepository {

    private final SeguroJpaRepository jpaRepository;
    private final UsuarioJpaRepository usuarioJpaRepository;

    @Override
    public Seguro guardar(Seguro seguro) {
        UsuarioEntity usuario = usuarioJpaRepository.getReferenceById(seguro.getIdUsuario());
        SeguroEntity entity = SeguroEntity.builder()
                .usuario(usuario)
                .estado(seguro.getEstado())
                .build();
        SeguroEntity saved = jpaRepository.save(entity);
        return Seguro.builder()
                .idSeguro(saved.getIdSeguro())
                .idUsuario(saved.getUsuario().getIdUsuario())
                .estado(saved.getEstado())
                .build();
    }
}
