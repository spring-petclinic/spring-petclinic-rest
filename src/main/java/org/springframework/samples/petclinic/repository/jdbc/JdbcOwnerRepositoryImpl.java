package org.springframework.samples.petclinic.repository.jdbc;

import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.repository.OwnerRepository;
import org.springframework.samples.petclinic.util.EntityUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.*;

@Repository
@Profile("jdbc")
public class JdbcOwnerRepositoryImpl implements OwnerRepository {

    private final NamedParameterJdbcTemplate jdbc;
    private final SimpleJdbcInsert insertOwner;

    public JdbcOwnerRepositoryImpl(DataSource dataSource) {
        this.jdbc = new NamedParameterJdbcTemplate(dataSource);
        this.insertOwner = new SimpleJdbcInsert(dataSource)
                .withTableName("owners")
                .usingGeneratedKeyColumns("id");
    }

    /*
     * =========================
     * FIND BY LAST NAME
     * =========================
     */
    @Override
    public Collection<Owner> findByLastName(String lastName) throws DataAccessException {

        Map<String, Object> params = Map.of("lastName", lastName + "%");

        List<Owner> owners = jdbc.query(
                "SELECT id, first_name, last_name, address, city, telephone " +
                        "FROM owners WHERE last_name LIKE :lastName",
                params,
                BeanPropertyRowMapper.newInstance(Owner.class));

        owners.forEach(this::loadPetsAndVisits);
        return owners;
    }

    /*
     * =========================
     * FIND BY ID
     * =========================
     */
    @Override
    public Owner findById(int id) throws DataAccessException {
        try {
            Owner owner = jdbc.queryForObject(
                    "SELECT id, first_name, last_name, address, city, telephone " +
                            "FROM owners WHERE id=:id",
                    Map.of("id", id),
                    BeanPropertyRowMapper.newInstance(Owner.class));
            loadPetsAndVisits(owner);
            return owner;
        } catch (EmptyResultDataAccessException ex) {
            throw new ObjectRetrievalFailureException(Owner.class, id);
        }
    }

    /*
     * =========================
     * SAVE OWNER
     * =========================
     */
    @Override
    public void save(Owner owner) throws DataAccessException {

        BeanPropertySqlParameterSource ps = new BeanPropertySqlParameterSource(owner);

        if (owner.isNew()) {
            Number key = insertOwner.executeAndReturnKey(ps);
            owner.setId(key.intValue());
        } else {
            jdbc.update(
                    "UPDATE owners SET first_name=:firstName, last_name=:lastName, " +
                            "address=:address, city=:city, telephone=:telephone " +
                            "WHERE id=:id",
                    ps);
        }
    }

    /*
     * =========================
     * FIND ALL (NO PAGINATION)
     * =========================
     */
    @Override
    public Collection<Owner> findAll() throws DataAccessException {

        List<Owner> owners = jdbc.query(
                "SELECT id, first_name, last_name, address, city, telephone FROM owners",
                BeanPropertyRowMapper.newInstance(Owner.class));

        owners.forEach(this::loadPetsAndVisits);
        return owners;
    }

    /*
     * =========================
     * FIND ALL (PAGINATION) ✅
     * =========================
     */
    @Override
    public Page<Owner> findAll(Pageable pageable) throws DataAccessException {

        Map<String, Object> params = new HashMap<>();
        params.put("limit", pageable.getPageSize());
        params.put("offset", pageable.getOffset());

        List<Owner> owners = jdbc.query(
                "SELECT id, first_name, last_name, address, city, telephone " +
                        "FROM owners ORDER BY last_name LIMIT :limit OFFSET :offset",
                params,
                BeanPropertyRowMapper.newInstance(Owner.class));

        owners.forEach(this::loadPetsAndVisits);

        Integer total = jdbc.queryForObject(
                "SELECT COUNT(*) FROM owners",
                Collections.emptyMap(),
                Integer.class);

        return new PageImpl<>(owners, pageable, total);
    }

    /*
     * =========================
     * DELETE OWNER (CASCADE)
     * =========================
     */
    @Override
    @Transactional
    public void delete(Owner owner) throws DataAccessException {

        for (Pet pet : owner.getPets()) {

            for (Visit visit : pet.getVisits()) {
                jdbc.update(
                        "DELETE FROM visits WHERE id=:id",
                        Map.of("id", visit.getId()));
            }

            jdbc.update(
                    "DELETE FROM pets WHERE id=:id",
                    Map.of("id", pet.getId()));
        }

        jdbc.update(
                "DELETE FROM owners WHERE id=:id",
                Map.of("id", owner.getId()));
    }

    /*
     * =========================
     * INTERNAL HELPERS
     * =========================
     */
    private void loadPetsAndVisits(Owner owner) {

        List<JdbcPet> pets = jdbc.query(
                "SELECT pets.id AS pets_id, name, birth_date, type_id, owner_id, " +
                        "visits.id AS visit_id, visit_date, description, " +
                        "visits.pet_id AS visits_pet_id " +
                        "FROM pets LEFT JOIN visits ON pets.id = visits.pet_id " +
                        "WHERE owner_id=:id",
                Map.of("id", owner.getId()),
                new JdbcPetVisitExtractor());

        Collection<PetType> types = getPetTypes();

        for (JdbcPet pet : pets) {
            pet.setType(EntityUtils.getById(types, PetType.class, pet.getTypeId()));
            owner.addPet(pet);
        }
    }

    private Collection<PetType> getPetTypes() {
        return jdbc.query(
                "SELECT id, name FROM types",
                BeanPropertyRowMapper.newInstance(PetType.class));
    }
}