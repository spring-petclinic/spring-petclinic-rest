package org.springframework.samples.petclinic.service.support;

import java.util.function.Supplier;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.orm.ObjectRetrievalFailureException;

/**
 * Utility helper for retrieving entities while tolerating missing results across repository implementations.
 */
public final class EntityFinder {

    private EntityFinder() {
        // utility
    }

    public static <T> T findOrNull(Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (ObjectRetrievalFailureException | EmptyResultDataAccessException ex) {
            return null;
        }
    }
}
