package com.logistica.application.security;

import com.logistica.domain.exceptions.AccesoDenegadoException;
import com.logistica.domain.models.Liquidacion;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class UsuarioAutenticado {

    private final String usuarioId;
    private final Collection<? extends GrantedAuthority> authorities;

    private UsuarioAutenticado(String usuarioId, Collection<? extends GrantedAuthority> authorities) {
        this.usuarioId = usuarioId;
        this.authorities = authorities;
    }

    public static UsuarioAutenticado from(Authentication authentication) {
        return new UsuarioAutenticado(authentication.getName(), authentication.getAuthorities());
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public boolean tienePermisoGlobal() {
        return authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_GESTOR_FINANCIERO")
                        || a.getAuthority().equals("ROLE_ADMIN"));
    }

    public void verificarAcceso(Liquidacion liquidacion) {
        if (!tienePermisoGlobal() && !liquidacion.getUsuarioId().equals(usuarioId)) {
            throw new AccesoDenegadoException("No tiene permisos para visualizar esta liquidacion.");
        }
    }
}
