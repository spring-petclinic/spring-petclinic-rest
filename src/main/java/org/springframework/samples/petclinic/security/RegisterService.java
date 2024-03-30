package org.springframework.samples.petclinic.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

@Service
public class RegisterService {

    @Autowired
    UserDetailsManager usersManager;

    @Autowired
    PasswordEncoder passwordEncoder;

    public UserDetails registerNewUserAccount(UserDtoSecurity userDto) {

        UserDetails user = User.builder()
                .username(userDto.getUsername())
                .password(userDto.getPassword())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .roles(userDto.getRoles())
                .build();
        usersManager.createUser(user);
        return user;
    }

}
