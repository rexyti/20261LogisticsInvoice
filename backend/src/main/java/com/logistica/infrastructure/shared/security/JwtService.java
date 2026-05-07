package com.logistica.infrastructure.shared.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secret;

    // Default: 24 horas. Reemplazar con config por entorno cuando haya múltiples perfiles de expiración.
    @Value("${app.jwt.expiration-ms:86400000}")
    private long expirationMs;

    /**
     * Genera un JWT firmado con HMAC-SHA256.
     * Almacena roles SIN prefijo "ROLE_" — el filtro lo agrega al leer.
     *
     * @param usuarioId identificador único del usuario (subject del token)
     * @param roles     lista de roles en formato raw, p. ej. ["ADMIN", "TRANSPORTISTA"]
     */
    public String generarToken(String usuarioId, List<String> roles) {
        long ahora = System.currentTimeMillis();
        return Jwts.builder()
                .subject(usuarioId)
                .claim("roles", roles)
                .issuedAt(new Date(ahora))
                .expiration(new Date(ahora + expirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    public Claims extraerClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extraerUsuarioId(String token) {
        return extraerClaims(token).getSubject();
    }

    /**
     * Extrae roles del JWT como nombres RAW (sin prefijo "ROLE_").
     * El prefijo lo agrega JwtAuthenticationFilter con SimpleGrantedAuthority("ROLE_" + role).
     * Soporta claim "roles" (List) y "role" (String) para retrocompatibilidad.
     */
    @SuppressWarnings("unchecked")
    public List<String> extraerRoles(String token) {
        Claims claims = extraerClaims(token);

        Object rolesClaim = claims.get("roles");
        if (rolesClaim instanceof List<?> list) {
            return list.stream()
                    .map(Object::toString)
                    .map(this::sinPrefijoRole)
                    .toList();
        }

        String roleClaim = claims.get("role", String.class);
        if (roleClaim != null) {
            return List.of(sinPrefijoRole(roleClaim));
        }

        return List.of();
    }

    public boolean esTokenValido(String token) {
        try {
            extraerClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /** Normaliza: si el JWT ya guardó "ROLE_ADMIN" por error, devuelve "ADMIN". */
    private String sinPrefijoRole(String role) {
        return role.startsWith("ROLE_") ? role.substring(5) : role;
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
