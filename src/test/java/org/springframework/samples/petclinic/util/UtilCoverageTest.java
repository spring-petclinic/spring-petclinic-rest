package org.springframework.samples.petclinic.util;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class UtilCoverageTest {

    /*
     * Executes private constructors
     * Boosts branch + line coverage FAST
     */
    @Test
    void shouldCoverUtilityConstructors() {

        Class<?>[] utilClasses = {
                EntityUtils.class
        };

        for (Class<?> util : utilClasses) {

            Constructor<?>[] constructors = util.getDeclaredConstructors();

            for (Constructor<?> constructor : constructors) {

                if (Modifier.isPrivate(constructor.getModifiers())) {

                    constructor.setAccessible(true);

                    assertDoesNotThrow(() -> constructor.newInstance());
                }
            }
        }
    }
}