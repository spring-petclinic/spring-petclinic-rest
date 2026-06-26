package org.springframework.samples.petclinic.rest.endpoint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.mapper.PetMapper;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.repository.PetRepository;
import org.springframework.samples.petclinic.repository.PetTypeRepository;
import org.springframework.samples.petclinic.rest.dto.PetDto;
import org.springframework.samples.petclinic.rest.dto.PetPageDto;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.officefloor.web.ObjectResponse;

@Validated
public class PetEndpoints {

    public void listPets(
            PetRepository petRepository,
            PetMapper petMapper,
            ObjectResponse<ResponseEntity<List<PetDto>>> response) {
        Collection<Pet> pets = petRepository.findAll();
        if (pets.isEmpty()) {
            response.send(ResponseEntity.notFound().build());
            return;
        }
        response.send(ResponseEntity.ok(new ArrayList<>(petMapper.toPetsDto(pets))));
    }

    public void getPet(
            @PathVariable(name = "petId") Integer petId,
            PetRepository petRepository,
            PetMapper petMapper,
            ObjectResponse<ResponseEntity<PetDto>> response) {
        Pet pet = petRepository.findById(petId);
        if (pet == null) {
            response.send(ResponseEntity.notFound().build());
            return;
        }
        response.send(ResponseEntity.ok(petMapper.toPetDto(pet)));
    }

    public void updatePet(
            @PathVariable(name = "petId") Integer petId,
            @Valid @RequestBody PetDto petDto,
            PetRepository petRepository,
            PetTypeRepository petTypeRepository,
            PetMapper petMapper,
            ObjectResponse<ResponseEntity<PetDto>> response) {
        Pet currentPet = petRepository.findById(petId);
        if (currentPet == null) {
            response.send(ResponseEntity.notFound().build());
            return;
        }
        currentPet.setBirthDate(petDto.getBirthDate());
        currentPet.setName(petDto.getName());
        currentPet.setType(petMapper.toPetType(petDto.getType()));
        PetType petType = petTypeRepository.findById(currentPet.getType().getId());
        currentPet.setType(petType);
        petRepository.save(currentPet);
        response.send(new ResponseEntity<>(petMapper.toPetDto(currentPet), HttpStatus.NO_CONTENT));
    }

    public void deletePet(
            @PathVariable(name = "petId") Integer petId,
            PetRepository petRepository,
            ObjectResponse<ResponseEntity<PetDto>> response) {
        Pet pet = petRepository.findById(petId);
        if (pet == null) {
            response.send(ResponseEntity.notFound().build());
            return;
        }
        petRepository.delete(pet);
        response.send(new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    public void listPetsPage(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size,
            PetRepository petRepository,
            PetMapper petMapper,
            ObjectResponse<ResponseEntity<PetPageDto>> response) {
        int pageNumber = page == null ? 0 : page;
        int pageSize = size == null ? 20 : size;
        Page<Pet> pets = petRepository.findAll(PageRequest.of(pageNumber, pageSize, Sort.by("id")));
        response.send(ResponseEntity.ok(petMapper.toPetPageDto(pets)));
    }
}
