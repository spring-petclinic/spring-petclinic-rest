package org.springframework.samples.petclinic.repository.jpa;

import java.util.Collection;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.repository.PetRepository;
import org.springframework.stereotype.Repository;

@Repository
@Profile("jpa")
public class JpaPetRepositoryImpl implements PetRepository {

    @PersistenceContext
    private EntityManager em;

    // -------------------------------------------------------------
    // PET TYPES
    // -------------------------------------------------------------
    @Override
    public List<PetType> findPetTypes() throws DataAccessException {

        TypedQuery<PetType> query = em.createQuery(
                "SELECT ptype FROM PetType ptype ORDER BY ptype.name",
                PetType.class);

        return query.getResultList();
    }

    // -------------------------------------------------------------
    // FIND BY ID
    // -------------------------------------------------------------
    @Override
    public Pet findById(int id) throws DataAccessException {
        return em.find(Pet.class, id);
    }

    // -------------------------------------------------------------
    // SAVE
    // -------------------------------------------------------------
    @Override
    public void save(Pet pet) throws DataAccessException {

        if (pet.getId() == null) {
            em.persist(pet);
        } else {
            em.merge(pet);
        }
    }

    // -------------------------------------------------------------
    // FIND ALL (NON PAGINATED)
    // -------------------------------------------------------------
    @Override
    public Collection<Pet> findAll() throws DataAccessException {

        TypedQuery<Pet> query = em.createQuery(
                "SELECT pet FROM Pet pet",
                Pet.class);

        return query.getResultList();
    }

    // -------------------------------------------------------------
    // 🔥 PAGINATION (THIS WAS YOUR CI BLOCKER)
    // -------------------------------------------------------------
    @Override
    public Page<Pet> findAll(Pageable pageable) throws DataAccessException {

        TypedQuery<Pet> query = em.createQuery(
                "SELECT pet FROM Pet pet",
                Pet.class);

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<Pet> pets = query.getResultList();

        Long total = em.createQuery(
                "SELECT COUNT(pet) FROM Pet pet",
                Long.class).getSingleResult();

        return new PageImpl<>(pets, pageable, total);
    }

    // -------------------------------------------------------------
    // DELETE
    // -------------------------------------------------------------
    @Override
    public void delete(Pet pet) throws DataAccessException {

        Pet managedPet = em.contains(pet) ? pet : em.merge(pet);
        em.remove(managedPet);
    }
}
