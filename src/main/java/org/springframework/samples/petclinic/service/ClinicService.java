package org.springframework.samples.petclinic.service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.samples.petclinic.model.*;

public interface ClinicService {

	/* ===================== PET ===================== */

	Pet findPetById(int id) throws DataAccessException;

	/**
	 * ✅ REQUIRED — DO NOT DELETE
	 * Old controllers still use this.
	 */
	Collection<Pet> findAllPets() throws DataAccessException;

	/**
	 * ✅ NEW — Pagination support
	 */
	Page<Pet> findAllPets(Pageable pageable) throws DataAccessException;

	void savePet(Pet pet) throws DataAccessException;

	void deletePet(Pet pet) throws DataAccessException;

	/* ===================== VISIT ===================== */

	Collection<Visit> findVisitsByPetId(int petId) throws DataAccessException;

	Visit findVisitById(int visitId) throws DataAccessException;

	Collection<Visit> findAllVisits() throws DataAccessException;

	void saveVisit(Visit visit) throws DataAccessException;

	void deleteVisit(Visit visit) throws DataAccessException;

	/* ===================== VET ===================== */

	Vet findVetById(int id) throws DataAccessException;

	Collection<Vet> findVets() throws DataAccessException;

	Collection<Vet> findAllVets() throws DataAccessException;

	void saveVet(Vet vet) throws DataAccessException;

	void deleteVet(Vet vet) throws DataAccessException;

	/* ===================== OWNER ===================== */

	Owner findOwnerById(int id) throws DataAccessException;

	/**
	 * ✅ REQUIRED legacy method
	 */
	Collection<Owner> findAllOwners() throws DataAccessException;

	/**
	 * ✅ NEW pagination
	 */
	Page<Owner> findAllOwners(Pageable pageable) throws DataAccessException;

	Collection<Owner> findOwnerByLastName(String lastName) throws DataAccessException;

	void saveOwner(Owner owner) throws DataAccessException;

	void deleteOwner(Owner owner) throws DataAccessException;

	/* ===================== PET TYPE ===================== */

	PetType findPetTypeById(int petTypeId) throws DataAccessException;

	Collection<PetType> findAllPetTypes() throws DataAccessException;

	Collection<PetType> findPetTypes() throws DataAccessException;

	void savePetType(PetType petType) throws DataAccessException;

	void deletePetType(PetType petType) throws DataAccessException;

	/* ===================== SPECIALTY ===================== */

	Specialty findSpecialtyById(int specialtyId) throws DataAccessException;

	Collection<Specialty> findAllSpecialties() throws DataAccessException;

	List<Specialty> findSpecialtiesByNameIn(Set<String> names) throws DataAccessException;

	void saveSpecialty(Specialty specialty) throws DataAccessException;

	void deleteSpecialty(Specialty specialty) throws DataAccessException;
}
