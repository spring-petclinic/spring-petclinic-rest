package org.springframework.samples.petclinic.util;

import org.junit.jupiter.api.Test;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.samples.petclinic.model.Owner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EntityUtilsTest {

    // ✅ HAPPY PATH
    @Test
    void shouldReturnMatchingEntity() {

        Owner o1 = createOwner(1);
        Owner o2 = createOwner(2);

        Owner found = EntityUtils.getById(
                List.of(o1, o2),
                Owner.class,
                2);

        assertThat(found).isSameAs(o2);
    }

    // ✅ NOT FOUND
    @Test
    void shouldThrowWhenIdNotFound() {

        Owner o1 = createOwner(1);

        assertThatThrownBy(() -> EntityUtils.getById(
                List.of(o1),
                Owner.class,
                99)).isInstanceOf(ObjectRetrievalFailureException.class);
    }

    // ✅ DUPLICATE IDS (covers loop branch)
    @Test
    void shouldReturnFirstMatchWhenDuplicatesExist() {

        Owner o1 = createOwner(10);
        Owner o2 = createOwner(10);

        Owner found = EntityUtils.getById(
                Arrays.asList(o1, o2),
                Owner.class,
                10);

        assertThat(found).isSameAs(o1);
    }

    // ✅ EMPTY COLLECTION
    @Test
    void shouldThrowWhenCollectionEmpty() {

        assertThatThrownBy(() -> EntityUtils.getById(
                Collections.emptyList(),
                Owner.class,
                1)).isInstanceOf(ObjectRetrievalFailureException.class);
    }

    // ⭐ VERY IMPORTANT — covers loop fully
    @Test
    void shouldSearchThroughEntireCollection() {

        Owner o1 = createOwner(1);
        Owner o2 = createOwner(2);
        Owner o3 = createOwner(3);

        Owner found = EntityUtils.getById(
                List.of(o1, o2, o3),
                Owner.class,
                3);

        assertThat(found).isSameAs(o3);
    }

    @Test
    void shouldThrowWhenIdNegative() {

        Owner owner = createOwner(1);

        assertThatThrownBy(() -> EntityUtils.getById(
                List.of(owner),
                Owner.class,
                -1)).isInstanceOf(Exception.class);
    }

    private Owner createOwner(int id) {
        Owner o = new Owner();
        o.setId(id);
        return o;
    }
}
