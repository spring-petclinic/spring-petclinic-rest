package org.springframework.samples.petclinic.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * @author Michael Isvy
 *         Simple test to make sure that Bean Validation is working
 *         (useful when upgrading to a new version of Hibernate Validator/ Bean Validation)
 */
class ValidatorTests {

    private Validator createValidator() {
        LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
        localValidatorFactoryBean.afterPropertiesSet();
        return localValidatorFactoryBean;
    }

    @Test
    void shouldNotValidateWhenFirstNameEmpty() {

        LocaleContextHolder.setLocale(Locale.ENGLISH);
        Person person = new Person();
        person.setFirstName("");
        person.setLastName("smith");

        Validator validator = createValidator();
        Set<ConstraintViolation<Person>> constraintViolations = validator.validate(person);

        assertThat(constraintViolations.size()).isEqualTo(1);
        ConstraintViolation<Person> violation = constraintViolations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("firstName");
        assertThat(violation.getMessage()).isEqualTo("must not be empty");
    }

    @Test
    void shouldNotValidateWhenPetBirthDateNull() {

        LocaleContextHolder.setLocale(Locale.ENGLISH);
        Pet pet = new Pet();
        pet.setName("Leo");

        Validator validator = createValidator();
        Set<ConstraintViolation<Pet>> constraintViolations = validator.validate(pet);

        assertThat(constraintViolations.size()).isEqualTo(1);
        ConstraintViolation<Pet> violation = constraintViolations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("birthDate");
        assertThat(violation.getMessage()).isEqualTo("must not be null");
    }

    @Test
    void shouldNotValidateWhenVisitDateNull() {

        LocaleContextHolder.setLocale(Locale.ENGLISH);
        Visit visit = new Visit();
        visit.setDescription("rabies shot");

        Validator validator = createValidator();
        Set<ConstraintViolation<Visit>> constraintViolations = validator.validate(visit);

        assertThat(constraintViolations.size()).isEqualTo(1);
        ConstraintViolation<Visit> violation = constraintViolations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("date");
        assertThat(violation.getMessage()).isEqualTo("must not be null");
    }

}
