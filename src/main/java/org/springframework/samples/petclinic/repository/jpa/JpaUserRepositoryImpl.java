package org.springframework.samples.petclinic.repository.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.model.User;
import org.springframework.samples.petclinic.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
@Profile("jpa")
public class JpaUserRepositoryImpl implements UserRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void save(User user) throws DataAccessException {
        if (this.em.find(User.class, user.getUsername()) == null) {
            this.em.persist(user);
        } else {
            this.em.merge(user);
        }
    }

    @Override
    public Collection<User> findAll() throws DataAccessException {
        Query query = this.em.createQuery("SELECT user FROM User user");
        return query.getResultList();
    }
}
