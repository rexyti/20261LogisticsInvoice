package com.logistica.infrastructure.auth.web;

import com.logistica.application.auth.dtos.request.LoginRequestDTO;
import com.logistica.application.auth.dtos.response.LoginResponseDTO;
import com.logistica.infrastructure.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Obtención de tokens JWT")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(
            summary = "Login",
            description = """
                    Retorna un JWT Bearer.
                    **Credenciales de prueba:**
                    - admin@test.com / 123456  → rol ADMIN
                    - gestor@test.com / 123456  → rol GESTOR_FINANCIERO
                    - transportista@test.com / 123456  → rol TRANSPORTISTA
                    """
    )
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
