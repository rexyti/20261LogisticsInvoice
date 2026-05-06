package com.logistica.application.visualizarLiquidacion.security;

import com.logistica.domain.visualizarLiquidacion.exceptions.AccesoDenegadoException;
import com.logistica.domain.visualizarLiquidacion.models.Liquidacion;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class VisualizarLiquidacionUsuarioAutenticado {

    private final String usuarioId;
    private final Collection<? extends GrantedAuthority> authorities;

    private VisualizarLiquidacionUsuarioAutenticado(String usuarioId, Collection<? extends GrantedAuthority> authorities) {
        this.usuarioId = usuarioId;
        this.authorities = authorities;
    }

    public static VisualizarLiquidacionUsuarioAutenticado from(Authentication authentication) {
        return new VisualizarLiquidacionUsuarioAutenticado(authentication.getName(), authentication.getAuthorities());
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
