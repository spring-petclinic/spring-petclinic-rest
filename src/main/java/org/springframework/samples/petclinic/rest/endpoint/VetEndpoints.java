package org.springframework.samples.petclinic.rest.endpoint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.mapper.SpecialtyMapper;
import org.springframework.samples.petclinic.mapper.VetMapper;
import org.springframework.samples.petclinic.model.Specialty;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.repository.SpecialtyRepository;
import org.springframework.samples.petclinic.repository.VetRepository;
import org.springframework.samples.petclinic.rest.dto.VetDto;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.util.UriComponentsBuilder;

import net.officefloor.web.ObjectResponse;

@Validated
public class VetEndpoints {

    public void listVets(
            VetRepository vetRepository,
            VetMapper vetMapper,
            ObjectResponse<ResponseEntity<List<VetDto>>> response) {
        Collection<Vet> vets = vetRepository.findAll();
        if (vets.isEmpty()) {
            response.send(ResponseEntity.notFound().build());
            return;
        }
        response.send(ResponseEntity.ok(new ArrayList<>(vetMapper.toVetDtos(vets))));
    }

    public void getVet(
            @PathVariable(name = "vetId") Integer vetId,
            VetRepository vetRepository,
            VetMapper vetMapper,
            ObjectResponse<ResponseEntity<VetDto>> response) {
        Vet vet = vetRepository.findById(vetId);
        if (vet == null) {
            response.send(ResponseEntity.notFound().build());
            return;
        }
        response.send(ResponseEntity.ok(vetMapper.toVetDto(vet)));
    }

    public void addVet(
            @Valid @RequestBody VetDto vetDto,
            VetRepository vetRepository,
            SpecialtyRepository specialtyRepository,
            VetMapper vetMapper,
            ObjectResponse<ResponseEntity<VetDto>> response) {
        Vet vet = vetMapper.toVet(vetDto);
        if (vet.getNrOfSpecialties() > 0) {
            List<Specialty> vetSpecialties = specialtyRepository.findSpecialtiesByNameIn(
                    vet.getSpecialties().stream().map(Specialty::getName).collect(Collectors.toSet()));
            vet.setSpecialties(vetSpecialties);
        }
        vetRepository.save(vet);
        String location = UriComponentsBuilder.newInstance()
                .path("/api/vets/{id}").buildAndExpand(vet.getId()).toUriString();
        response.send(ResponseEntity.created(java.net.URI.create(location)).body(vetMapper.toVetDto(vet)));
    }

    public void updateVet(
            @PathVariable(name = "vetId") Integer vetId,
            @Valid @RequestBody VetDto vetDto,
            VetRepository vetRepository,
            SpecialtyRepository specialtyRepository,
            VetMapper vetMapper,
            SpecialtyMapper specialtyMapper,
            ObjectResponse<ResponseEntity<VetDto>> response) {
        Vet currentVet = vetRepository.findById(vetId);
        if (currentVet == null) {
            response.send(ResponseEntity.notFound().build());
            return;
        }
        currentVet.setFirstName(vetDto.getFirstName());
        currentVet.setLastName(vetDto.getLastName());
        currentVet.clearSpecialties();
        for (Specialty spec : specialtyMapper.toSpecialtys(vetDto.getSpecialties())) {
            currentVet.addSpecialty(spec);
        }
        if (currentVet.getNrOfSpecialties() > 0) {
            List<Specialty> vetSpecialties = specialtyRepository.findSpecialtiesByNameIn(
                    currentVet.getSpecialties().stream().map(Specialty::getName).collect(Collectors.toSet()));
            currentVet.setSpecialties(vetSpecialties);
        }
        vetRepository.save(currentVet);
        response.send(new ResponseEntity<>(vetMapper.toVetDto(currentVet), HttpStatus.NO_CONTENT));
    }

    public void deleteVet(
            @PathVariable(name = "vetId") Integer vetId,
            VetRepository vetRepository,
            ObjectResponse<ResponseEntity<VetDto>> response) {
        Vet vet = vetRepository.findById(vetId);
        if (vet == null) {
            response.send(ResponseEntity.notFound().build());
            return;
        }
        vetRepository.delete(vet);
        response.send(new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }
}
