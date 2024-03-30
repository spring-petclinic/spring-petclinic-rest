package org.springframework.samples.petclinic.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegisterController {

    @Autowired
    RegisterService registerService;

    @PostMapping(value = "/register")
    public String register(@RequestBody UserDtoSecurity userDto) {
        registerService.registerNewUserAccount(userDto);
        return "Registered!";
    }

    @GetMapping(value = "/register")
    public String register() {
        return "Registered!";
    }

    @PostMapping("/simplepost")
    public String simple_p() {
        return "Hello post.";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/simplepost")
    public String simple() {
        return "Hello get.";
    }

}
