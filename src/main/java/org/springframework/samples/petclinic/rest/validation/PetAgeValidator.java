package org.springframework.samples.petclinic.rest.validation;

import java.time.LocalDate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PetAgeValidator implements ConstraintValidator<PetAgeValidation, LocalDate> {

    @Override
    public boolean isValid(LocalDate birthDate, ConstraintValidatorContext context) {

        if (birthDate == null) {
            return true; 
        }

        LocalDate today = LocalDate.now();

        if (birthDate.isAfter(today)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                "Birth date cannot be in the future"
            ).addConstraintViolation();
            return false;
        }

        if (birthDate.isBefore(today.minusYears(50))) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                "Birth date cannot be older than 50 years"
            ).addConstraintViolation();
            return false;
        }

        return true;
    }
}