package com.logistica.contratos.application.validators;

import com.logistica.contratos.application.dtos.request.ContratoRequestDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FechasContratoValidator implements ConstraintValidator<ValidFechasContrato, ContratoRequestDTO> {

    @Override
    public boolean isValid(ContratoRequestDTO dto, ConstraintValidatorContext context) {
        if (dto.getFechaInicio() == null || dto.getFechaFinal() == null) {
            return true;
        }
        boolean valido = dto.getFechaFinal().isAfter(dto.getFechaInicio());
        if (!valido) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            "La fecha final debe ser estrictamente mayor a la fecha de inicio")
                    .addPropertyNode("fechaFinal")
                    .addConstraintViolation();
        }
        return valido;
    }
}