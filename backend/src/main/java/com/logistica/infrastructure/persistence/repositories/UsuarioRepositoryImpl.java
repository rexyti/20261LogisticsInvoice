package com.logistica.infrastructure.persistence.repositories;

import com.logistica.domain.models.Usuario;
import com.logistica.domain.repositories.UsuarioRepository;
import com.logistica.infrastructure.persistence.entities.UsuarioEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UsuarioRepositoryImpl implements UsuarioRepository {

    private final UsuarioJpaRepository jpaRepository;

    @Override
    public Usuario guardar(Usuario usuario) {
        UsuarioEntity entity = UsuarioEntity.builder()
                .nombre(usuario.getNombre())
                .build();
        UsuarioEntity saved = jpaRepository.save(entity);
        return Usuario.builder()
                .idUsuario(saved.getIdUsuario())
                .nombre(saved.getNombre())
                .build();
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return jpaRepository.findById(id)
                .map(e -> Usuario.builder()
                        .idUsuario(e.getIdUsuario())
                        .nombre(e.getNombre())
                        .build());
    }
}
