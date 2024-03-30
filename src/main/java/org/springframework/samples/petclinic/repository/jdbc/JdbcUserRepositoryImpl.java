package org.springframework.samples.petclinic.repository.jdbc;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.samples.petclinic.model.Role;
import org.springframework.samples.petclinic.model.User;
import org.springframework.samples.petclinic.repository.UserRepository;
import org.springframework.stereotype.Repository;

@Repository
@Profile("jdbc")
public class JdbcUserRepositoryImpl implements UserRepository {

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private SimpleJdbcInsert insertUser;

    @Autowired
    public JdbcUserRepositoryImpl(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.insertUser = new SimpleJdbcInsert(dataSource).withTableName("users");
    }

    @Override
    public User save(User user) throws DataAccessException {

        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);

        try {
            findByUsername(user.getUsername());
            this.namedParameterJdbcTemplate.update("UPDATE users SET password=:password, enabled=:enabled WHERE username=:username", parameterSource);
        } catch (EmptyResultDataAccessException e) {
            this.insertUser.execute(parameterSource);
        } finally {
            updateUserRoles(user);
        }

        return user;
    }

    @Override
    public Optional<User> findByUsername(String username) {

        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        return Optional.ofNullable(this.namedParameterJdbcTemplate.queryForObject("SELECT * FROM users WHERE username=:username",
        params, BeanPropertyRowMapper.newInstance(User.class)));
    }

    private void updateUserRoles(User user) {
        Map<String, Object> params = new HashMap<>();
        params.put("username", user.getUsername());
        this.namedParameterJdbcTemplate.update("DELETE FROM authorities WHERE username=:username", params);
        for (Role role : user.getRoles()) {
            params.put("authority", role.getName());
            if (role.getName() != null) {
                this.namedParameterJdbcTemplate.update("INSERT INTO authorities(username, authority) VALUES (:username, :authority)", params);
            }
        }
    }
}
