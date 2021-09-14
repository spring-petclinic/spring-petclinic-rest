/*
 * Copyright 2016-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.samples.petclinic.rest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.dto.PetDto;
import org.springframework.samples.petclinic.dto.PetTypeDto;
import org.springframework.samples.petclinic.mapper.PetMapper;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Collection;

/**
 * @author Vitaliy Fedoriv
 */

@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
@RequestMapping("api/pets")
public class PetRestController {

<<<<<<< HEAD
    @Autowired
    private ClinicService clinicService;

    @Autowired
    private PetMapper petMapper;

    @PreAuthorize( "hasRole(@roles.OWNER_ADMIN)" )
	@RequestMapping(value = "/{petId}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<PetDto> getPet(@PathVariable("petId") int petId){
		PetDto pet = petMapper.toPetDto(this.clinicService.findPetById(petId));
		if(pet == null){
			return new ResponseEntity<PetDto>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<PetDto>(pet, HttpStatus.OK);
	}
    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @RequestMapping(value = "/{petId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Pet> getPet(@PathVariable("petId") int petId) {
        Pet pet = this.clinicService.findPetById(petId);
        if (pet == null) {
            return new ResponseEntity<Pet>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Pet>(pet, HttpStatus.OK);
=======
    private final ClinicService clinicService;

    private final PetMapper petMapper;

    public PetRestController(ClinicService clinicService, PetMapper petMapper) {
        this.clinicService = clinicService;
        this.petMapper = petMapper;
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @RequestMapping(value = "/{petId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<PetDto> getPet(@PathVariable("petId") int petId) {
        PetDto pet = petMapper.toPetDto(this.clinicService.findPetById(petId));
        if (pet == null) {
            return new ResponseEntity<PetDto>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<PetDto>(pet, HttpStatus.OK);
>>>>>>> a4ca4b1... feat!: enable dto genertation using openapi generator. First tests done with Pets
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
<<<<<<< HEAD
    public ResponseEntity<Collection<Pet>> getPets() {
        Collection<Pet> pets = this.clinicService.findAllPets();
        if (pets.isEmpty()) {
            return new ResponseEntity<Collection<Pet>>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Collection<Pet>>(pets, HttpStatus.OK);
=======
    public ResponseEntity<Collection<PetDto>> getPets() {
        Collection<PetDto> pets = petMapper.toPetsDto(this.clinicService.findAllPets());
        if (pets.isEmpty()) {
            return new ResponseEntity<Collection<PetDto>>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Collection<PetDto>>(pets, HttpStatus.OK);
>>>>>>> a4ca4b1... feat!: enable dto genertation using openapi generator. First tests done with Pets
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @RequestMapping(value = "/pettypes", method = RequestMethod.GET, produces = "application/json")
<<<<<<< HEAD
    public ResponseEntity<Collection<PetType>> getPetTypes() {
        return new ResponseEntity<Collection<PetType>>(this.clinicService.findPetTypes(), HttpStatus.OK);
=======
    public ResponseEntity<Collection<PetTypeDto>> getPetTypes() {
        return new ResponseEntity<Collection<PetTypeDto>>(petMapper.toPetTypeDtos(this.clinicService.findPetTypes()), HttpStatus.OK);
>>>>>>> a4ca4b1... feat!: enable dto genertation using openapi generator. First tests done with Pets
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json")
<<<<<<< HEAD
    public ResponseEntity<Pet> addPet(@RequestBody @Valid Pet pet, BindingResult bindingResult, UriComponentsBuilder ucBuilder) {
        BindingErrorsResponse errors = new BindingErrorsResponse();
        HttpHeaders headers = new HttpHeaders();
        if (bindingResult.hasErrors() || (pet == null)) {
            errors.addAllErrors(bindingResult);
            headers.add("errors", errors.toJSON());
            return new ResponseEntity<Pet>(headers, HttpStatus.BAD_REQUEST);
        }
        this.clinicService.savePet(pet);
        headers.setLocation(ucBuilder.path("/api/pets/{id}").buildAndExpand(pet.getId()).toUri());
        return new ResponseEntity<Pet>(pet, headers, HttpStatus.CREATED);
=======
    public ResponseEntity<PetDto> addPet(@RequestBody @Valid PetDto petDto, BindingResult bindingResult, UriComponentsBuilder ucBuilder) {
        BindingErrorsResponse errors = new BindingErrorsResponse();
        HttpHeaders headers = new HttpHeaders();
        if (bindingResult.hasErrors() || (petDto == null)) {
            errors.addAllErrors(bindingResult);
            headers.add("errors", errors.toJSON());
            return new ResponseEntity<>(headers, HttpStatus.BAD_REQUEST);
        }
        Pet pet = petMapper.toPet(petDto);
        this.clinicService.savePet(pet);
        petDto.setId(pet.getId());
        headers.setLocation(ucBuilder.path("/api/pets/{id}").buildAndExpand(petDto.getId()).toUri());
        return new ResponseEntity<>(petDto, headers, HttpStatus.CREATED);
>>>>>>> a4ca4b1... feat!: enable dto genertation using openapi generator. First tests done with Pets
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @RequestMapping(value = "/{petId}", method = RequestMethod.PUT, produces = "application/json")
<<<<<<< HEAD
    public ResponseEntity<Pet> updatePet(@PathVariable("petId") int petId, @RequestBody @Valid Pet pet, BindingResult bindingResult) {
=======
    public ResponseEntity<PetDto> updatePet(@PathVariable("petId") int petId, @RequestBody @Valid PetDto pet, BindingResult bindingResult) {
>>>>>>> a4ca4b1... feat!: enable dto genertation using openapi generator. First tests done with Pets
        BindingErrorsResponse errors = new BindingErrorsResponse();
        HttpHeaders headers = new HttpHeaders();
        if (bindingResult.hasErrors() || (pet == null)) {
            errors.addAllErrors(bindingResult);
            headers.add("errors", errors.toJSON());
<<<<<<< HEAD
            return new ResponseEntity<Pet>(headers, HttpStatus.BAD_REQUEST);
        }
        Pet currentPet = this.clinicService.findPetById(petId);
        if (currentPet == null) {
            return new ResponseEntity<Pet>(HttpStatus.NOT_FOUND);
        }
        currentPet.setBirthDate(pet.getBirthDate());
        currentPet.setName(pet.getName());
        currentPet.setType(pet.getType());
        currentPet.setOwner(pet.getOwner());
        this.clinicService.savePet(currentPet);
        return new ResponseEntity<Pet>(currentPet, HttpStatus.NO_CONTENT);
=======
            return new ResponseEntity<>(headers, HttpStatus.BAD_REQUEST);
        }
        Pet currentPet = this.clinicService.findPetById(petId);
        if (currentPet == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        currentPet.setBirthDate(pet.getBirthDate());
        currentPet.setName(pet.getName());
        currentPet.setType(petMapper.toPetType(pet.getType()));
        currentPet.setOwner(petMapper.toOwner(pet.getOwner()));
        this.clinicService.savePet(currentPet);
        return new ResponseEntity<>(petMapper.toPetDto(currentPet), HttpStatus.NO_CONTENT);
>>>>>>> a4ca4b1... feat!: enable dto genertation using openapi generator. First tests done with Pets
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @RequestMapping(value = "/{petId}", method = RequestMethod.DELETE, produces = "application/json")
    @Transactional
    public ResponseEntity<Void> deletePet(@PathVariable("petId") int petId) {
        Pet pet = this.clinicService.findPetById(petId);
        if (pet == null) {
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }
        this.clinicService.deletePet(pet);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }


}
