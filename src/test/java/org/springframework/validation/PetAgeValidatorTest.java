package org.springframework.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.samples.petclinic.rest.validation.PetAgeValidator;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PetAgeValidatorTest {

    private final PetAgeValidator validator = new PetAgeValidator();

    @Test
    void shouldReturnFalseWhenBirthDateIsInFuture() {
        LocalDate futureDate = LocalDate.now().plusDays(1);

        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        when(context.buildConstraintViolationWithTemplate(anyString()))
                .thenReturn(mock(ConstraintValidatorContext.ConstraintViolationBuilder.class));

        boolean result = validator.isValid(futureDate, context);

        assertFalse(result);
    }

    @Test
    void shouldReturnFalseWhenBirthDateIsOlderThan50Years() {
        LocalDate tooOldDate = LocalDate.now().minusYears(51);

        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        when(context.buildConstraintViolationWithTemplate(anyString()))
                .thenReturn(mock(ConstraintValidatorContext.ConstraintViolationBuilder.class));

        boolean result = validator.isValid(tooOldDate, context);

        assertFalse(result);
    }

    @Test
    void shouldReturnTrueWhenBirthDateIsValid() {
        LocalDate validDate = LocalDate.now().minusYears(10);

        boolean result = validator.isValid(validDate, null);

        assertTrue(result);
    }

    @Test
    void shouldReturnTrueWhenBirthDateIsNull() {
        boolean result = validator.isValid(null, null);

        assertTrue(result);
    }
}