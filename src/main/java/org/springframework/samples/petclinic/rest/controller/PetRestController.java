package org.springframework.samples.petclinic.rest.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.mapper.PetMapper;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.rest.dto.PetDto;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
@RequestMapping("/api")
public class PetRestController {

    private final ClinicService clinicService;
    private final PetMapper petMapper;

    public PetRestController(ClinicService clinicService, PetMapper petMapper) {
        this.clinicService = clinicService;
        this.petMapper = petMapper;
    }

    /*
     * ✅ SUPPORTS BOTH PAGINATED + NON PAGINATED
     */
    @GetMapping(value = "/pets", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    public ResponseEntity<List<PetDto>> listPets(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        Collection<Pet> pets;

        // ⭐ If pagination params exist → use pageable
        if (page != null || size != null) {

            int pageNumber = (page != null) ? page : 0;
            int pageSize = (size != null) ? size : 10;

            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<Pet> petPage = clinicService.findAllPets(pageable);

            if (petPage == null || petPage.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            pets = petPage.getContent();

        } else {
            // ⭐ fallback for OLD tests
            pets = clinicService.findAllPets();

            if (pets == null || pets.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }

        List<PetDto> petDtos = new ArrayList<>(petMapper.toPetsDto(pets));

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(petDtos);
    }

    /*
     * SINGLE PET
     */
    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @GetMapping(value = "/pets/{petId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PetDto> getPet(@PathVariable Integer petId) {

        Pet pet = clinicService.findPetById(petId);

        if (pet == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(petMapper.toPetDto(pet));
    }

    /*
     * UPDATE PET
     */
    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @PutMapping(value = "/pets/{petId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updatePet(
            @PathVariable Integer petId,
            @RequestBody PetDto petDto) {

        if (petDto == null || petDto.getName() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Pet pet = clinicService.findPetById(petId);

        if (pet == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        pet.setName(petDto.getName());
        pet.setBirthDate(petDto.getBirthDate());
        pet.setType(petMapper.toPetType(petDto.getType()));

        clinicService.savePet(pet);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .contentType(MediaType.APPLICATION_JSON)
                .build();
    }

    /*
     * DELETE PET
     */
    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @DeleteMapping("/pets/{petId}")
    public ResponseEntity<Void> deletePet(@PathVariable Integer petId) {

        Pet pet = clinicService.findPetById(petId);

        if (pet == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        clinicService.deletePet(pet);

        return ResponseEntity.noContent().build();
    }
}
