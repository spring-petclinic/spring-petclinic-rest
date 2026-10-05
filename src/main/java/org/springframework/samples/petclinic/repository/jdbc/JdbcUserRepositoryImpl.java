package org.springframework.samples.petclinic.repository.jdbc;

import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.samples.petclinic.model.Role;
import org.springframework.samples.petclinic.model.User;
import org.springframework.samples.petclinic.repository.UserRepository;
import org.springframework.stereotype.Repository;

@DependsOnDatabaseInitialization
@Repository
public class JdbcUserRepositoryImpl implements UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcUserRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(User user) {
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM users WHERE username = ?", Integer.class, user.getUsername());
        if (count != null && count > 0) {
            jdbcTemplate.update("UPDATE users SET password = ?, enabled = ? WHERE username = ?",
                user.getPassword(), user.getEnabled(), user.getUsername());
        } else {
            jdbcTemplate.update("INSERT INTO users (username, password, enabled) VALUES (?, ?, ?)",
                user.getUsername(), user.getPassword(), user.getEnabled());
        }
        updateUserRoles(user);
    }

    private void updateUserRoles(User user) {
        jdbcTemplate.update("DELETE FROM roles WHERE username = ?", user.getUsername());
        for (Role role : user.getRoles()) {
            if (role.getName() != null) {
                jdbcTemplate.update("INSERT INTO roles (username, role) VALUES (?, ?)",
                    user.getUsername(), role.getName());
            }
        }
    }
}
