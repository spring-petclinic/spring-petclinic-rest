package org.springframework.samples.petclinic.rest.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PetAgeValidator.class)
@Documented
public @interface PetAgeValidation {

    String message() default "Birth date must not be in the future or older than 50 years";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}