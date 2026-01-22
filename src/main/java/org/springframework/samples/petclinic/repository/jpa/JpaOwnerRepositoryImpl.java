package org.springframework.samples.petclinic.repository.jpa;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.repository.OwnerRepository;

/**
 * JPA implementation of the OwnerRepository.
 *
 * Uses EntityManager directly instead of Spring Data JPA
 * to keep control over queries and fetch strategies.
 */
@Repository
@Profile("jpa")
@Transactional
public class JpaOwnerRepositoryImpl implements OwnerRepository {

    @PersistenceContext
    private EntityManager em;

    /**
     * Find owners whose last name starts with the given value.
     * Fetch pets eagerly to avoid N+1 problem.
     */
    @Override
    public Collection<Owner> findByLastName(String lastName) throws DataAccessException {
        TypedQuery<Owner> query = em.createQuery(
                """
                        SELECT DISTINCT o
                        FROM Owner o
                        LEFT JOIN FETCH o.pets
                        WHERE o.lastName LIKE :lastName
                        """,
                Owner.class);
        query.setParameter("lastName", lastName + "%");
        return query.getResultList();
    }

    /**
     * Find owner by id with pets eagerly loaded.
     */
    @Override
    public Owner findById(int id) throws DataAccessException {
        TypedQuery<Owner> query = em.createQuery(
                """
                        SELECT o
                        FROM Owner o
                        LEFT JOIN FETCH o.pets
                        WHERE o.id = :id
                        """,
                Owner.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    /**
     * Save or update an owner.
     */
    @Override
    public void save(Owner owner) throws DataAccessException {
        if (owner.getId() == null) {
            em.persist(owner);
        } else {
            em.merge(owner);
        }
    }

    /**
     * Fetch all owners (without pagination).
     */
    @Override
    public Collection<Owner> findAll() throws DataAccessException {
        TypedQuery<Owner> query = em.createQuery("SELECT o FROM Owner o", Owner.class);
        return query.getResultList();
    }

    /**
     * Fetch owners with pagination support.
     */
    @Override
    public Page<Owner> findAll(Pageable pageable) throws DataAccessException {

        TypedQuery<Owner> query = em.createQuery("SELECT o FROM Owner o ORDER BY o.lastName", Owner.class);

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<Owner> owners = query.getResultList();

        Long total = em.createQuery(
                "SELECT COUNT(o) FROM Owner o",
                Long.class).getSingleResult();

        return new PageImpl<>(owners, pageable, total);
    }

    /**
     * Delete an owner safely.
     */
    @Override
    public void delete(Owner owner) throws DataAccessException {
        Owner managedOwner = em.contains(owner) ? owner : em.merge(owner);
        em.remove(managedOwner);
    }
}
