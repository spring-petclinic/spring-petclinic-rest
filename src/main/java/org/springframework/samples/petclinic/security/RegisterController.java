package org.springframework.samples.petclinic.security;

import org.springframework.beans.factory.annotation.Autowired;
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
        System.out.println("register user : " + userDto.getUsername());
        registerService.registerNewUserAccount(userDto);

        return "Registered!";
    }

    @GetMapping(value = "/register")
    public String register() {
        UserDtoSecurity userDto = new UserDtoSecurity();
        userDto.setUsername("user_");
        userDto.setPassword("11");
        userDto.setRoles("USER");
        registerService.registerNewUserAccount(userDto);

        return "Registered!";
    }

}
