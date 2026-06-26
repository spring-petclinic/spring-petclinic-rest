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
import org.springframework.samples.petclinic.mapper.OwnerMapper;
import org.springframework.samples.petclinic.mapper.PetMapper;
import org.springframework.samples.petclinic.mapper.VisitMapper;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.repository.OwnerRepository;
import org.springframework.samples.petclinic.repository.PetRepository;
import org.springframework.samples.petclinic.repository.PetTypeRepository;
import org.springframework.samples.petclinic.repository.VisitRepository;
import org.springframework.samples.petclinic.rest.dto.OwnerDto;
import org.springframework.samples.petclinic.rest.dto.OwnerFieldsDto;
import org.springframework.samples.petclinic.rest.dto.OwnerPageDto;
import org.springframework.samples.petclinic.rest.dto.PetDto;
import org.springframework.samples.petclinic.rest.dto.PetFieldsDto;
import org.springframework.samples.petclinic.rest.dto.VisitDto;
import org.springframework.samples.petclinic.rest.dto.VisitFieldsDto;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import net.officefloor.web.ObjectResponse;

@Validated
public class OwnerEndpoints {

    public void listOwners(
            @RequestParam(name = "lastName", required = false) String lastName,
            OwnerRepository ownerRepository,
            OwnerMapper ownerMapper,
            ObjectResponse<ResponseEntity<List<OwnerDto>>> response) {
        Collection<Owner> owners;
        if (lastName != null) {
            owners = ownerRepository.findByLastName(lastName);
        } else {
            owners = ownerRepository.findAll();
        }
        if (owners.isEmpty()) {
            response.send(ResponseEntity.notFound().build());
            return;
        }
        response.send(ResponseEntity.ok(ownerMapper.toOwnerDtoCollection(owners)));
    }

    public void addOwner(
            @Valid @RequestBody OwnerFieldsDto ownerFieldsDto,
            OwnerRepository ownerRepository,
            OwnerMapper ownerMapper,
            ObjectResponse<ResponseEntity<OwnerDto>> response) {
        Owner owner = ownerMapper.toOwner(ownerFieldsDto);
        ownerRepository.save(owner);
        OwnerDto ownerDto = ownerMapper.toOwnerDto(owner);
        String location = UriComponentsBuilder.newInstance()
                .path("/api/owners/{id}").buildAndExpand(owner.getId()).toUriString();
        response.send(ResponseEntity.created(java.net.URI.create(location)).body(ownerDto));
    }

    public void getOwner(
            @PathVariable(name = "ownerId") Integer ownerId,
            OwnerRepository ownerRepository,
            OwnerMapper ownerMapper,
            ObjectResponse<ResponseEntity<OwnerDto>> response) {
        Owner owner = ownerRepository.findById(ownerId);
        if (owner == null) {
            response.send(ResponseEntity.notFound().build());
            return;
        }
        response.send(ResponseEntity.ok(ownerMapper.toOwnerDto(owner)));
    }

    public void updateOwner(
            @PathVariable(name = "ownerId") Integer ownerId,
            @Valid @RequestBody OwnerFieldsDto ownerFieldsDto,
            OwnerRepository ownerRepository,
            OwnerMapper ownerMapper,
            ObjectResponse<ResponseEntity<OwnerDto>> response) {
        Owner currentOwner = ownerRepository.findById(ownerId);
        if (currentOwner == null) {
            response.send(ResponseEntity.notFound().build());
            return;
        }
        currentOwner.setAddress(ownerFieldsDto.getAddress());
        currentOwner.setCity(ownerFieldsDto.getCity());
        currentOwner.setFirstName(ownerFieldsDto.getFirstName());
        currentOwner.setLastName(ownerFieldsDto.getLastName());
        currentOwner.setTelephone(ownerFieldsDto.getTelephone());
        ownerRepository.save(currentOwner);
        response.send(new ResponseEntity<>(ownerMapper.toOwnerDto(currentOwner), HttpStatus.NO_CONTENT));
    }

    public void deleteOwner(
            @PathVariable(name = "ownerId") Integer ownerId,
            OwnerRepository ownerRepository,
            ObjectResponse<ResponseEntity<OwnerDto>> response) {
        Owner owner = ownerRepository.findById(ownerId);
        if (owner == null) {
            response.send(ResponseEntity.notFound().build());
            return;
        }
        ownerRepository.delete(owner);
        response.send(new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    public void addPetToOwner(
            @PathVariable(name = "ownerId") Integer ownerId,
            @Valid @RequestBody PetFieldsDto petFieldsDto,
            OwnerRepository ownerRepository,
            PetRepository petRepository,
            PetTypeRepository petTypeRepository,
            PetMapper petMapper,
            ObjectResponse<ResponseEntity<PetDto>> response) {
        Owner owner = ownerRepository.findById(ownerId);
        if (owner == null) {
            response.send(ResponseEntity.notFound().build());
            return;
        }
        Pet pet = petMapper.toPet(petFieldsDto);
        owner.setId(ownerId);
        pet.setOwner(owner);
        PetType petType = petTypeRepository.findById(pet.getType().getId());
        pet.setType(petType);
        petRepository.save(pet);
        PetDto petDto = petMapper.toPetDto(pet);
        String location = UriComponentsBuilder.newInstance()
                .path("/api/pets/{id}").buildAndExpand(pet.getId()).toUriString();
        response.send(ResponseEntity.created(java.net.URI.create(location)).body(petDto));
    }

    public void getOwnersPet(
            @PathVariable(name = "ownerId") Integer ownerId,
            @PathVariable(name = "petId") Integer petId,
            OwnerRepository ownerRepository,
            PetMapper petMapper,
            ObjectResponse<ResponseEntity<PetDto>> response) {
        Owner owner = ownerRepository.findById(ownerId);
        if (owner != null) {
            Pet pet = owner.getPet(petId);
            if (pet != null) {
                response.send(ResponseEntity.ok(petMapper.toPetDto(pet)));
                return;
            }
        }
        response.send(ResponseEntity.notFound().build());
    }

    public void updateOwnersPet(
            @PathVariable(name = "ownerId") Integer ownerId,
            @PathVariable(name = "petId") Integer petId,
            @Valid @RequestBody PetFieldsDto petFieldsDto,
            OwnerRepository ownerRepository,
            PetRepository petRepository,
            PetMapper petMapper,
            ObjectResponse<ResponseEntity<Void>> response) {
        Owner currentOwner = ownerRepository.findById(ownerId);
        if (currentOwner != null) {
            Pet currentPet = petRepository.findById(petId);
            if (currentPet != null) {
                currentPet.setBirthDate(petFieldsDto.getBirthDate());
                currentPet.setName(petFieldsDto.getName());
                currentPet.setType(petMapper.toPetType(petFieldsDto.getType()));
                petRepository.save(currentPet);
                response.send(new ResponseEntity<>(HttpStatus.NO_CONTENT));
                return;
            }
        }
        response.send(ResponseEntity.notFound().build());
    }

    public void addVisitToOwner(
            @PathVariable(name = "ownerId") Integer ownerId,
            @PathVariable(name = "petId") Integer petId,
            @Valid @RequestBody VisitFieldsDto visitFieldsDto,
            VisitRepository visitRepository,
            VisitMapper visitMapper,
            ObjectResponse<ResponseEntity<VisitDto>> response) {
        Visit visit = visitMapper.toVisit(visitFieldsDto);
        Pet pet = new Pet();
        pet.setId(petId);
        visit.setPet(pet);
        visitRepository.save(visit);
        VisitDto visitDto = visitMapper.toVisitDto(visit);
        String location = UriComponentsBuilder.newInstance()
                .path("/api/visits/{id}").buildAndExpand(visit.getId()).toUriString();
        response.send(ResponseEntity.created(java.net.URI.create(location)).body(visitDto));
    }

    public void listOwnersPage(
            @RequestParam(name = "lastName", required = false) String lastName,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size,
            OwnerRepository ownerRepository,
            OwnerMapper ownerMapper,
            ObjectResponse<ResponseEntity<OwnerPageDto>> response) {
        int pageNumber = page == null ? 0 : page;
        int pageSize = size == null ? 20 : size;
        Page<Owner> owners;
        if (lastName != null) {
            owners = ownerRepository.findByLastName(lastName, PageRequest.of(pageNumber, pageSize, Sort.by("id")));
        } else {
            owners = ownerRepository.findAll(PageRequest.of(pageNumber, pageSize, Sort.by("id")));
        }
        response.send(ResponseEntity.ok(ownerMapper.toOwnerPageDto(owners)));
    }
}
