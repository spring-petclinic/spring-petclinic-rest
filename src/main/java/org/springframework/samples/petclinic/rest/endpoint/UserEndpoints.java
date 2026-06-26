package org.springframework.samples.petclinic.rest.endpoint;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.mapper.UserMapper;
import org.springframework.samples.petclinic.model.Role;
import org.springframework.samples.petclinic.model.User;
import org.springframework.samples.petclinic.repository.UserRepository;
import org.springframework.samples.petclinic.rest.dto.UserDto;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

import net.officefloor.web.ObjectResponse;

@Validated
public class UserEndpoints {

    public void addUser(
            @Valid @RequestBody UserDto userDto,
            UserRepository userRepository,
            UserMapper userMapper,
            ObjectResponse<ResponseEntity<UserDto>> response) {
        User user = userMapper.toUser(userDto);
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            throw new IllegalArgumentException("User must have at least a role set!");
        }
        for (Role role : user.getRoles()) {
            if (!role.getName().startsWith("ROLE_")) {
                role.setName("ROLE_" + role.getName());
            }
            if (role.getUser() == null) {
                role.setUser(user);
            }
        }
        userRepository.save(user);
        response.send(ResponseEntity.status(201).body(userMapper.toUserDto(user)));
    }
}
