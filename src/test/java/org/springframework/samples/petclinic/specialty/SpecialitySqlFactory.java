package org.springframework.samples.petclinic.specialty;

public class SpecialitySqlFactory {

    private SpecialitySqlFactory() {
    }

    public static String insertRadiology() {
        return "INSERT INTO specialties VALUES (1, 'radiology');";
    }

}
