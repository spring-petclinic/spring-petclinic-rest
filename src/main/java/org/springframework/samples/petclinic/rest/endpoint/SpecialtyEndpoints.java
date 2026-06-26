package org.springframework.samples.petclinic.rest.endpoint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.mapper.SpecialtyMapper;
import org.springframework.samples.petclinic.model.Specialty;
import org.springframework.samples.petclinic.repository.SpecialtyRepository;
import org.springframework.samples.petclinic.rest.dto.SpecialtyDto;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.util.UriComponentsBuilder;

import net.officefloor.web.ObjectResponse;

@Validated
public class SpecialtyEndpoints {

    public void listSpecialties(
            SpecialtyRepository specialtyRepository,
            SpecialtyMapper specialtyMapper,
            ObjectResponse<ResponseEntity<List<SpecialtyDto>>> response) {
        Collection<Specialty> specialties = specialtyRepository.findAll();
        if (specialties.isEmpty()) {
            response.send(ResponseEntity.notFound().build());
            return;
        }
        response.send(ResponseEntity.ok(new ArrayList<>(specialtyMapper.toSpecialtyDtos(new ArrayList<>(specialties)))));
    }

    public void getSpecialty(
            @PathVariable(name = "specialtyId") Integer specialtyId,
            SpecialtyRepository specialtyRepository,
            SpecialtyMapper specialtyMapper,
            ObjectResponse<ResponseEntity<SpecialtyDto>> response) {
        Specialty specialty = specialtyRepository.findById(specialtyId);
        if (specialty == null) {
            response.send(ResponseEntity.notFound().build());
            return;
        }
        response.send(ResponseEntity.ok(specialtyMapper.toSpecialtyDto(specialty)));
    }

    public void addSpecialty(
            @Valid @RequestBody SpecialtyDto specialtyDto,
            SpecialtyRepository specialtyRepository,
            SpecialtyMapper specialtyMapper,
            ObjectResponse<ResponseEntity<SpecialtyDto>> response) {
        Specialty specialty = specialtyMapper.toSpecialty(specialtyDto);
        specialtyRepository.save(specialty);
        String location = UriComponentsBuilder.newInstance()
                .path("/api/specialties/{id}").buildAndExpand(specialty.getId()).toUriString();
        response.send(ResponseEntity.created(java.net.URI.create(location)).body(specialtyMapper.toSpecialtyDto(specialty)));
    }

    public void updateSpecialty(
            @PathVariable(name = "specialtyId") Integer specialtyId,
            @Valid @RequestBody SpecialtyDto specialtyDto,
            SpecialtyRepository specialtyRepository,
            SpecialtyMapper specialtyMapper,
            ObjectResponse<ResponseEntity<SpecialtyDto>> response) {
        Specialty currentSpecialty = specialtyRepository.findById(specialtyId);
        if (currentSpecialty == null) {
            response.send(ResponseEntity.notFound().build());
            return;
        }
        currentSpecialty.setName(specialtyDto.getName());
        specialtyRepository.save(currentSpecialty);
        response.send(new ResponseEntity<>(specialtyMapper.toSpecialtyDto(currentSpecialty), HttpStatus.NO_CONTENT));
    }

    public void deleteSpecialty(
            @PathVariable(name = "specialtyId") Integer specialtyId,
            SpecialtyRepository specialtyRepository,
            ObjectResponse<ResponseEntity<SpecialtyDto>> response) {
        Specialty specialty = specialtyRepository.findById(specialtyId);
        if (specialty == null) {
            response.send(ResponseEntity.notFound().build());
            return;
        }
        specialtyRepository.delete(specialty);
        response.send(new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }
}
