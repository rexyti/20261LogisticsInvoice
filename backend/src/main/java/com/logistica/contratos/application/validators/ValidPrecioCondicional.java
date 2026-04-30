package com.logistica.contratos.application.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = PrecioCondicionalValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPrecioCondicional {
    String message() default "El precio no cumple con las reglas del tipo de contrato";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}