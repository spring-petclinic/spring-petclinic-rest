package org.springframework.samples.petclinic.service;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.User;
import org.springframework.samples.petclinic.model.Role;
import org.springframework.samples.petclinic.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void saveUser(User user) {
        if(user.getRoles() == null || user.getRoles().isEmpty()) {
            throw new IllegalArgumentException("User must have at least a role set!");
        }

        entityManager.createQuery("DELETE FROM Role r WHERE r.user.username = :username")
            .setParameter("username", user.getUsername())
            .executeUpdate();
        entityManager.flush();

        for (Role role : user.getRoles()) {
            if(!role.getName().startsWith("ROLE_")) {
                role.setName("ROLE_" + role.getName());
            }
            if(role.getUser() == null) {
                role.setUser(user);
            }
        }

        Map<String, Role> uniqueRoles = new LinkedHashMap<>();
        for (Role role : user.getRoles()) {
            uniqueRoles.put(role.getName(), role);
        }
        user.setRoles(new java.util.LinkedHashSet<>(uniqueRoles.values()));

        userRepository.save(user);
    }
}