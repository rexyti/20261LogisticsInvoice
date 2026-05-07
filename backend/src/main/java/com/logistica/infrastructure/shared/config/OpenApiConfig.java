package com.logistica.infrastructure.shared.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configura Swagger UI para solicitar un Bearer JWT en el botón "Authorize".
 * Todos los endpoints marcados con @SecurityRequirement("bearerAuth") muestran
 * el candado y envían el header Authorization: Bearer <token>.
 */
@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Pega aquí el token obtenido en POST /auth/login"
)
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Logística API")
                        .version("1.0")
                        .description("Backend para la gestión de liquidaciones logísticas"))
                // Aplica el esquema bearerAuth globalmente a todos los endpoints
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
