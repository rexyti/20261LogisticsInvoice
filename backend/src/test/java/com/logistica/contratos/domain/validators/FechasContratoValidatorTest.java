package com.logistica.contratos.domain.validators;

import com.logistica.contratos.application.dtos.request.ContratoRequestDTO;
import com.logistica.contratos.application.dtos.request.SeguroRequestDTO;
import com.logistica.contratos.domain.enums.ContratosTipoVehiculo;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FechasContratoValidatorTest {

    private static Validator validator;

    @BeforeAll
    static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private ContratoRequestDTO dtoValido() {
        SeguroRequestDTO seguro = new SeguroRequestDTO();
        seguro.setNumeroPoliza("POL-001");
        seguro.setEstado("VIGENTE");

        ContratoRequestDTO dto = new ContratoRequestDTO();
        dto.setIdContrato("CONT-001");
        dto.setTipoContrato("MENSAJERIA");
        dto.setTransportistaId(UUID.randomUUID());
        dto.setEsPorParada(true);
        dto.setPrecioParadas(new BigDecimal("15.50"));
        dto.setTipoVehiculo(ContratosTipoVehiculo.VAN);
        dto.setFechaInicio(LocalDateTime.of(2026, 1, 1, 0, 0));
        dto.setFechaFinal(LocalDateTime.of(2026, 12, 31, 0, 0));
        dto.setSeguro(seguro);
        return dto;
    }

    @Test
    @DisplayName("T008 - Falla cuando fechaFinal es anterior a fechaInicio")
    void debeRechazarFechaFinalAnteriorAInicio() {
        ContratoRequestDTO dto = dtoValido();
        dto.setFechaInicio(LocalDateTime.of(2026, 6, 1, 0, 0));
        dto.setFechaFinal(LocalDateTime.of(2026, 1, 1, 0, 0));

        Set<ConstraintViolation<ContratoRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("fechaFinal")
                        && v.getMessage().contains("estrictamente mayor")
        );
    }

    @Test
    @DisplayName("T008 - Falla cuando fechaFinal es igual a fechaInicio")
    void debeRechazarFechaFinalIgualAInicio() {
        ContratoRequestDTO dto = dtoValido();
        LocalDateTime mismaFecha = LocalDateTime.of(2026, 6, 1, 0, 0);
        dto.setFechaInicio(mismaFecha);
        dto.setFechaFinal(mismaFecha);

        Set<ConstraintViolation<ContratoRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("fechaFinal")
                        && v.getMessage().contains("estrictamente mayor")
        );
    }

    @Test
    @DisplayName("T008 - Pasa cuando fechaFinal es estrictamente posterior a fechaInicio")
    void debeAceptarFechaFinalPosteriorAInicio() {
        ContratoRequestDTO dto = dtoValido();

        Set<ConstraintViolation<ContratoRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).noneMatch(v ->
                v.getPropertyPath().toString().equals("fechaFinal")
                        && v.getMessage().contains("estrictamente mayor")
        );
    }

    @Test
    @DisplayName("T008 - Permite nulos (delegados a @NotNull)")
    void debePermitirNulosCuandoExistenOtrasValidaciones() {
        ContratoRequestDTO dto = dtoValido();
        dto.setFechaInicio(null);
        dto.setFechaFinal(null);

        Set<ConstraintViolation<ContratoRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).noneMatch(v ->
                v.getMessage().contains("estrictamente mayor")
        );
    }
}
