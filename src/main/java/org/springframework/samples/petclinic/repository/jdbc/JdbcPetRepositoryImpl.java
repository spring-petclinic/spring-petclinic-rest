package org.springframework.samples.petclinic.repository.jdbc;

import java.util.*;

import javax.sql.DataSource;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.samples.petclinic.model.*;
import org.springframework.samples.petclinic.repository.OwnerRepository;
import org.springframework.samples.petclinic.repository.PetRepository;
import org.springframework.samples.petclinic.repository.VisitRepository;
import org.springframework.samples.petclinic.util.EntityUtils;
import org.springframework.stereotype.Repository;

@Repository
@Profile("jdbc")
public class JdbcPetRepositoryImpl implements PetRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertPet;
    private final OwnerRepository ownerRepository;
    private final VisitRepository visitRepository;

    public JdbcPetRepositoryImpl(
            DataSource dataSource,
            OwnerRepository ownerRepository,
            VisitRepository visitRepository) {

        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

        this.insertPet = new SimpleJdbcInsert(dataSource)
                .withTableName("pets")
                .usingGeneratedKeyColumns("id");

        this.ownerRepository = ownerRepository;
        this.visitRepository = visitRepository;
    }

    // ---------------------------------------------------------
    // PET TYPES
    // ---------------------------------------------------------
    @Override
    public List<PetType> findPetTypes() throws DataAccessException {

        return jdbcTemplate.query(
                "SELECT id, name FROM types ORDER BY name",
                new HashMap<>(),
                BeanPropertyRowMapper.newInstance(PetType.class));
    }

    // ---------------------------------------------------------
    // FIND BY ID
    // ---------------------------------------------------------
    @Override
    public Pet findById(int id) throws DataAccessException {

        try {
            Map<String, Object> params = Map.of("id", id);

            Integer ownerId = jdbcTemplate.queryForObject(
                    "SELECT owner_id FROM pets WHERE id=:id",
                    params,
                    Integer.class);

            Owner owner = ownerRepository.findById(ownerId);

            return EntityUtils.getById(owner.getPets(), Pet.class, id);

        } catch (EmptyResultDataAccessException ex) {
            throw new ObjectRetrievalFailureException(Pet.class, id);
        }
    }

    // ---------------------------------------------------------
    // SAVE
    // ---------------------------------------------------------
    @Override
    public void save(Pet pet) throws DataAccessException {

        if (pet.isNew()) {

            Number newKey = insertPet.executeAndReturnKey(createParams(pet));
            pet.setId(newKey.intValue());

        } else {

            jdbcTemplate.update(
                    "UPDATE pets SET name=:name, birth_date=:birth_date, type_id=:type_id, owner_id=:owner_id WHERE id=:id",
                    createParams(pet));
        }
    }

    private MapSqlParameterSource createParams(Pet pet) {

        return new MapSqlParameterSource()
                .addValue("id", pet.getId())
                .addValue("name", pet.getName())
                .addValue("birth_date", pet.getBirthDate())
                .addValue("type_id", pet.getType().getId())
                .addValue("owner_id", pet.getOwner().getId());
    }

    // ---------------------------------------------------------
    // FIND ALL (NON PAGINATED)
    // ---------------------------------------------------------
    @Override
    public Collection<Pet> findAll() throws DataAccessException {

        List<JdbcPet> jdbcPets = jdbcTemplate.query(
                "SELECT pets.id AS pets_id, name, birth_date, type_id, owner_id FROM pets",
                new HashMap<>(),
                new JdbcPetRowMapper());

        Collection<PetType> petTypes = jdbcTemplate.query(
                "SELECT id, name FROM types",
                BeanPropertyRowMapper.newInstance(PetType.class));

        Collection<Owner> owners = jdbcTemplate.query(
                "SELECT id, first_name, last_name, address, city, telephone FROM owners",
                BeanPropertyRowMapper.newInstance(Owner.class));

        List<Pet> pets = new ArrayList<>();

        for (JdbcPet jdbcPet : jdbcPets) {

            jdbcPet.setType(EntityUtils.getById(petTypes, PetType.class, jdbcPet.getTypeId()));
            jdbcPet.setOwner(EntityUtils.getById(owners, Owner.class, jdbcPet.getOwnerId()));

            pets.add(jdbcPet);
        }

        return pets;
    }

    // ---------------------------------------------------------
    // 🔥 PAGINATION (CI SAFE)
    // ---------------------------------------------------------
    @Override
    public Page<Pet> findAll(Pageable pageable) throws DataAccessException {

        List<Pet> allPets = new ArrayList<>(findAll());

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), allPets.size());

        if (start > end) {
            return new PageImpl<>(Collections.emptyList(), pageable, allPets.size());
        }

        List<Pet> pageContent = allPets.subList(start, end);

        return new PageImpl<>(pageContent, pageable, allPets.size());
    }

    // ---------------------------------------------------------
    // DELETE
    // ---------------------------------------------------------
    @Override
    public void delete(Pet pet) throws DataAccessException {

        // delete visits first (FK safety)
        for (Visit visit : pet.getVisits()) {

            jdbcTemplate.update(
                    "DELETE FROM visits WHERE id=:id",
                    Map.of("id", visit.getId()));
        }

        jdbcTemplate.update(
                "DELETE FROM pets WHERE id=:id",
                Map.of("id", pet.getId()));
    }
}
