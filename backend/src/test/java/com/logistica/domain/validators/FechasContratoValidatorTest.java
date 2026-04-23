package com.logistica.domain.validators;

import com.logistica.application.dtos.request.ContratoRequestDTO;
import com.logistica.domain.enums.TipoContrato;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class FechasContratoValidatorTest {

    private static Validator validator;

    @BeforeAll
    static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private ContratoRequestDTO dtoValido() {
        return ContratoRequestDTO.builder()
                .idContrato("CONT-001")
                .tipoContrato(TipoContrato.POR_PARADA)
                .nombreConductor("Juan Pérez")
                .precioParadas(new BigDecimal("15.50"))
                .tipoVehiculo("CAMION")
                .fechaInicio(LocalDate.of(2026, 1, 1))
                .fechaFinal(LocalDate.of(2026, 12, 31))
                .estadoSeguro("VIGENTE")
                .build();
    }

    @Test
    @DisplayName("T008 - Falla cuando fechaFinal es anterior a fechaInicio")
    void debeRechazarFechaFinalAnteriorAInicio() {
        ContratoRequestDTO dto = dtoValido();
        dto.setFechaInicio(LocalDate.of(2026, 6, 1));
        dto.setFechaFinal(LocalDate.of(2026, 1, 1));

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
        LocalDate mismaFecha = LocalDate.of(2026, 6, 1);
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
