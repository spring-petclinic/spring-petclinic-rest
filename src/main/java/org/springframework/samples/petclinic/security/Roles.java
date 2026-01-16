package org.springframework.samples.petclinic.security;

import org.springframework.stereotype.Component;

@Component
public class Roles {

    //new

    public static final String OWNERADMIN = "ROLEOWNERADMIN";
    public static final String VETADMIN = "ROLEVETADMIN";
    public static final String ADMIN = "ROLEADMIN";
}
