package org.springframework.samples.petclinic.rest.endpoint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.mapper.PetTypeMapper;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.repository.PetTypeRepository;
import org.springframework.samples.petclinic.rest.dto.PetTypeDto;
import org.springframework.samples.petclinic.rest.dto.PetTypeFieldsDto;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.util.UriComponentsBuilder;

import net.officefloor.web.ObjectResponse;

@Validated
public class PetTypeEndpoints {

    public void listPetTypes(
            PetTypeRepository petTypeRepository,
            PetTypeMapper petTypeMapper,
            ObjectResponse<ResponseEntity<List<PetTypeDto>>> response) {
        Collection<PetType> petTypes = petTypeRepository.findAll();
        if (petTypes.isEmpty()) {
            response.send(ResponseEntity.notFound().build());
            return;
        }
        response.send(ResponseEntity.ok(petTypeMapper.toPetTypeDtos(new ArrayList<>(petTypes))));
    }

    public void getPetType(
            @PathVariable(name = "petTypeId") Integer petTypeId,
            PetTypeRepository petTypeRepository,
            PetTypeMapper petTypeMapper,
            ObjectResponse<ResponseEntity<PetTypeDto>> response) {
        PetType petType = petTypeRepository.findById(petTypeId);
        if (petType == null) {
            response.send(ResponseEntity.notFound().build());
            return;
        }
        response.send(ResponseEntity.ok(petTypeMapper.toPetTypeDto(petType)));
    }

    public void addPetType(
            @Valid @RequestBody PetTypeFieldsDto petTypeFieldsDto,
            PetTypeRepository petTypeRepository,
            PetTypeMapper petTypeMapper,
            ObjectResponse<ResponseEntity<PetTypeDto>> response) {
        PetType type = petTypeMapper.toPetType(petTypeFieldsDto);
        petTypeRepository.save(type);
        String location = UriComponentsBuilder.newInstance()
                .path("/api/pettypes/{id}").buildAndExpand(type.getId()).toUriString();
        response.send(ResponseEntity.created(java.net.URI.create(location)).body(petTypeMapper.toPetTypeDto(type)));
    }

    public void updatePetType(
            @PathVariable(name = "petTypeId") Integer petTypeId,
            @Valid @RequestBody PetTypeDto petTypeDto,
            PetTypeRepository petTypeRepository,
            PetTypeMapper petTypeMapper,
            ObjectResponse<ResponseEntity<PetTypeDto>> response) {
        PetType currentPetType = petTypeRepository.findById(petTypeId);
        if (currentPetType == null) {
            response.send(ResponseEntity.notFound().build());
            return;
        }
        currentPetType.setName(petTypeDto.getName());
        petTypeRepository.save(currentPetType);
        response.send(new ResponseEntity<>(petTypeMapper.toPetTypeDto(currentPetType), HttpStatus.NO_CONTENT));
    }

    public void deletePetType(
            @PathVariable(name = "petTypeId") Integer petTypeId,
            PetTypeRepository petTypeRepository,
            ObjectResponse<ResponseEntity<PetTypeDto>> response) {
        PetType petType = petTypeRepository.findById(petTypeId);
        if (petType == null) {
            response.send(ResponseEntity.notFound().build());
            return;
        }
        petTypeRepository.delete(petType);
        response.send(new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }
}
