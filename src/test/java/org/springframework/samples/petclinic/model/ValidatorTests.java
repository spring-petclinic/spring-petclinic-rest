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
        assertThat(violation.getMessage()).isEqualTo("must not be blank");
    }

    @Test
    void shouldNotValidateWhenFirstNameBlank() {

        LocaleContextHolder.setLocale(Locale.ENGLISH);
        Person person = new Person();
        person.setFirstName("   ");
        person.setLastName("smith");

        Validator validator = createValidator();
        Set<ConstraintViolation<Person>> constraintViolations = validator.validate(person);

        assertThat(constraintViolations.size()).isEqualTo(1);
        ConstraintViolation<Person> violation = constraintViolations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("firstName");
        assertThat(violation.getMessage()).isEqualTo("must not be blank");
    }

    @Test
    void shouldNotValidateWhenLastNameBlank() {

        LocaleContextHolder.setLocale(Locale.ENGLISH);
        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("   ");

        Validator validator = createValidator();
        Set<ConstraintViolation<Person>> constraintViolations = validator.validate(person);

        assertThat(constraintViolations).hasSize(1);
        ConstraintViolation<Person> violation = constraintViolations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("lastName");
        assertThat(violation.getMessage()).isEqualTo("must not be blank");
    }

    @Test
    void shouldNotValidateWhenNamedEntityNameBlank() {

        LocaleContextHolder.setLocale(Locale.ENGLISH);
        PetType petType = new PetType();
        petType.setName("   ");

        Validator validator = createValidator();
        Set<ConstraintViolation<PetType>> constraintViolations = validator.validate(petType);

        assertThat(constraintViolations.size()).isEqualTo(1);
        ConstraintViolation<PetType> violation = constraintViolations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("name");
        assertThat(violation.getMessage()).isEqualTo("must not be blank");
    }

    @Test
    void shouldNotValidateWhenVisitDescriptionBlank() {

        LocaleContextHolder.setLocale(Locale.ENGLISH);
        Visit visit = new Visit();
        visit.setDescription("   ");

        Validator validator = createValidator();
        Set<ConstraintViolation<Visit>> constraintViolations = validator.validate(visit);

        assertThat(constraintViolations.size()).isEqualTo(1);
        ConstraintViolation<Visit> violation = constraintViolations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("description");
        assertThat(violation.getMessage()).isEqualTo("must not be blank");
    }

    @Test
    void shouldNotValidateWhenOwnerAddressOrCityBlank() {

        LocaleContextHolder.setLocale(Locale.ENGLISH);
        Owner owner = validOwner();
        owner.setAddress("   ");
        owner.setCity("\t ");

        Validator validator = createValidator();
        Set<ConstraintViolation<Owner>> constraintViolations = validator.validate(owner);

        assertThat(constraintViolations).hasSize(2);
        assertThat(constraintViolations).extracting(v -> v.getPropertyPath().toString())
            .containsExactlyInAnyOrder("address", "city");
        assertThat(constraintViolations).extracting(ConstraintViolation::getMessage)
            .containsOnly("must not be blank");
    }

    @Test
    void shouldNotValidateWhenOwnerTelephoneNull() {

        LocaleContextHolder.setLocale(Locale.ENGLISH);
        Owner owner = validOwner();
        owner.setTelephone(null);

        Validator validator = createValidator();
        Set<ConstraintViolation<Owner>> constraintViolations = validator.validate(owner);

        assertThat(constraintViolations.size()).isEqualTo(1);
        ConstraintViolation<Owner> violation = constraintViolations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("telephone");
        assertThat(violation.getMessage()).isEqualTo("must not be blank");
    }

    @Test
    void shouldNotValidateWhenOwnerTelephoneBlankOrNotTenDigits() {

        LocaleContextHolder.setLocale(Locale.ENGLISH);
        Validator validator = createValidator();

        Owner blank = validOwner();
        blank.setTelephone("   ");
        assertThat(validator.validate(blank)).extracting(ConstraintViolation::getMessage)
            .contains("must not be blank");

        Owner tooShort = validOwner();
        tooShort.setTelephone("123");
        Set<ConstraintViolation<Owner>> constraintViolations = validator.validate(tooShort);
        assertThat(constraintViolations.size()).isEqualTo(1);
        assertThat(constraintViolations.iterator().next().getMessage()).isEqualTo("Phone number must be exactly 10 digits");
    }

    private Owner validOwner() {
        Owner owner = new Owner();
        owner.setFirstName("George");
        owner.setLastName("Franklin");
        owner.setAddress("110 W. Liberty St.");
        owner.setCity("Madison");
        owner.setTelephone("6085551023");
        return owner;
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

}
