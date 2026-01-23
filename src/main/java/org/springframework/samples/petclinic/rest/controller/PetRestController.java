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
     * -------------------------------------------------
     * LIST PETS (PAGINATION)
     * -------------------------------------------------
     */
    @GetMapping(value = "/pets", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    public ResponseEntity<List<PetDto>> listPets(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        int pageNumber = (page != null) ? page : 0;
        int pageSize = (size != null) ? size : 10;

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Pet> petPage = clinicService.findAllPets(pageable);

        List<PetDto> petDtos = new ArrayList<>(
                petMapper.toPetsDto(petPage.getContent()));

        return ResponseEntity.ok(petDtos);
    }

    /*
     * -------------------------------------------------
     * GET PET
     * -------------------------------------------------
     */
    @GetMapping(value = "/pets/{petId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    public ResponseEntity<PetDto> getPet(@PathVariable Integer petId) {

        Pet pet = clinicService.findPetById(petId);
        if (pet == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(petMapper.toPetDto(pet));
    }

    /*
     * -------------------------------------------------
     * UPDATE PET
     * -------------------------------------------------
     */
    @PutMapping(value = "/pets/{petId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    public ResponseEntity<PetDto> updatePet(
            @PathVariable Integer petId,
            @RequestBody PetDto petDto) {

        Pet pet = clinicService.findPetById(petId);
        if (pet == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        pet.setName(petDto.getName());
        pet.setBirthDate(petDto.getBirthDate());
        pet.setType(petMapper.toPetType(petDto.getType()));

        clinicService.savePet(pet);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(petMapper.toPetDto(pet));
    }

    /*
     * -------------------------------------------------
     * DELETE PET
     * -------------------------------------------------
     */
    @DeleteMapping("/pets/{petId}")
    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    public ResponseEntity<Void> deletePet(@PathVariable Integer petId) {

        Pet pet = clinicService.findPetById(petId);
        if (pet == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        clinicService.deletePet(pet);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
