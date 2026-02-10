package org.springframework.samples.petclinic.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UtilSmokeTest {

    @Test
    void shouldLoadUtilClasses() throws Exception {

        // Force class loading
        Class<?> clazz = Class.forName(
                "org.springframework.samples.petclinic.util.EntityUtils");

        assertThat(clazz).isNotNull();
    }
}
