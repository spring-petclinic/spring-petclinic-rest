package org.springframework.samples.petclinic.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
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
        assertThat(violation.getMessage()).isEqualTo("must not be blank");
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
    void shouldNotValidateWhenFirstNameContainsOnlySpaces() {
        Person person = new Person();
        person.setFirstName("   ");
        person.setLastName("smith");

        Validator validator = createValidator();
        Set<ConstraintViolation<Person>> violations = validator.validate(person);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString())
            .isEqualTo("firstName");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t\n"})
    void shouldRejectBlankNames(String value) {
        Validator validator = createValidator();

        assertThat(validator.validateValue(NamedEntity.class, "name", value)).isNotEmpty();
        assertThat(validator.validateValue(Person.class, "firstName", value)).isNotEmpty();
        assertThat(validator.validateValue(Person.class, "lastName", value)).isNotEmpty();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t\n"})
    void shouldRejectBlankOwnerDetails(String value) {
        Validator validator = createValidator();

        assertThat(validator.validateValue(Owner.class, "address", value)).isNotEmpty();
        assertThat(validator.validateValue(Owner.class, "city", value)).isNotEmpty();
        assertThat(validator.validateValue(Owner.class, "telephone", value)).isNotEmpty();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t\n"})
    void shouldRejectBlankVisitDescription(String value) {
        Validator validator = createValidator();

        assertThat(validator.validateValue(Visit.class, "description", value)).isNotEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"123456789", "12345678901", "12345abcde", "+123456789"})
    void shouldRejectInvalidTelephone(String value) {
        Validator validator = createValidator();

        assertThat(validator.validateValue(Owner.class, "telephone", value)).isNotEmpty();
    }

    @Test
    void shouldAcceptValidDetails() {
        Validator validator = createValidator();

        assertThat(validator.validateValue(NamedEntity.class, "name", "Leo")).isEmpty();
        assertThat(validator.validateValue(Person.class, "firstName", "John")).isEmpty();
        assertThat(validator.validateValue(Person.class, "lastName", "Smith")).isEmpty();
        assertThat(validator.validateValue(Owner.class, "address", "10 Main Street")).isEmpty();
        assertThat(validator.validateValue(Owner.class, "city", "New Delhi")).isEmpty();
        assertThat(validator.validateValue(Owner.class, "telephone", "0123456789")).isEmpty();
        assertThat(validator.validateValue(Visit.class, "description", "Annual checkup")).isEmpty();
    }

}
