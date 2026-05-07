package com.logistica.infrastructure.auth.service;

import com.logistica.application.auth.dtos.request.LoginRequestDTO;
import com.logistica.application.auth.dtos.response.LoginResponseDTO;
import com.logistica.infrastructure.shared.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;

    @Value("${app.jwt.expiration-ms:86400000}")
    private long expirationMs;

    // ---------------------------------------------------------------------------
    // HARDCODE TEMPORAL — reemplazar por consulta a UserRepository / UserDetailsService
    // cuando se implemente la gestión de usuarios con base de datos.
    // Cada entrada: email -> [password, roles...]
    // ---------------------------------------------------------------------------
    private static final Map<String, HardcodedUser> USUARIOS_TEMPORALES = Map.of(
            "admin@test.com", new HardcodedUser("admin@test.com", "123456", List.of("ADMIN")),
            "gestor@test.com", new HardcodedUser("gestor@test.com", "123456", List.of("GESTOR_FINANCIERO")),
            "transportista@test.com", new HardcodedUser("transportista@test.com", "123456", List.of("TRANSPORTISTA"))
    );
    // ---------------------------------------------------------------------------

    /**
     * Autentica credenciales y devuelve un JWT firmado.
     * Lanza 401 si el email no existe o la contraseña no coincide.
     *
     * TODO: reemplazar USUARIOS_TEMPORALES por autenticación real contra BD.
     */
    public LoginResponseDTO login(LoginRequestDTO request) {
        HardcodedUser usuario = USUARIOS_TEMPORALES.get(request.getEmail());

        // TODO: reemplazar comparación de contraseña en texto plano por BCrypt.check()
        if (usuario == null || !usuario.password().equals(request.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }

        String token = jwtService.generarToken(usuario.email(), usuario.roles());

        return LoginResponseDTO.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(expirationMs / 1000)
                .build();
    }

    /** Record interno que representa un usuario hardcodeado. */
    private record HardcodedUser(String email, String password, List<String> roles) {}
}
