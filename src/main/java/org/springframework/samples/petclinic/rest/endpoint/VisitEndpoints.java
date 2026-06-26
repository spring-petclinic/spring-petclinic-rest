package org.springframework.samples.petclinic.rest.endpoint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.mapper.VisitMapper;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.repository.VisitRepository;
import org.springframework.samples.petclinic.rest.dto.VisitDto;
import org.springframework.samples.petclinic.rest.dto.VisitFieldsDto;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.util.UriComponentsBuilder;

import net.officefloor.web.ObjectResponse;

@Validated
public class VisitEndpoints {

    public void listVisits(
            VisitRepository visitRepository,
            VisitMapper visitMapper,
            ObjectResponse<ResponseEntity<List<VisitDto>>> response) {
        Collection<Visit> visits = visitRepository.findAll();
        if (visits.isEmpty()) {
            response.send(ResponseEntity.notFound().build());
            return;
        }
        response.send(ResponseEntity.ok(new ArrayList<>(visitMapper.toVisitsDto(new ArrayList<>(visits)))));
    }

    public void getVisit(
            @PathVariable(name = "visitId") Integer visitId,
            VisitRepository visitRepository,
            VisitMapper visitMapper,
            ObjectResponse<ResponseEntity<VisitDto>> response) {
        Visit visit = visitRepository.findById(visitId);
        if (visit == null) {
            response.send(ResponseEntity.notFound().build());
            return;
        }
        response.send(ResponseEntity.ok(visitMapper.toVisitDto(visit)));
    }

    public void addVisit(
            @Valid @RequestBody VisitDto visitDto,
            VisitRepository visitRepository,
            VisitMapper visitMapper,
            ObjectResponse<ResponseEntity<VisitDto>> response) {
        Visit visit = visitMapper.toVisit(visitDto);
        visitRepository.save(visit);
        visitDto = visitMapper.toVisitDto(visit);
        String location = UriComponentsBuilder.newInstance()
                .path("/api/visits/{id}").buildAndExpand(visit.getId()).toUriString();
        response.send(ResponseEntity.created(java.net.URI.create(location)).body(visitDto));
    }

    public void updateVisit(
            @PathVariable(name = "visitId") Integer visitId,
            @Valid @RequestBody VisitFieldsDto visitDto,
            VisitRepository visitRepository,
            VisitMapper visitMapper,
            ObjectResponse<ResponseEntity<VisitDto>> response) {
        Visit currentVisit = visitRepository.findById(visitId);
        if (currentVisit == null) {
            response.send(ResponseEntity.notFound().build());
            return;
        }
        currentVisit.setDate(visitDto.getDate());
        currentVisit.setDescription(visitDto.getDescription());
        visitRepository.save(currentVisit);
        response.send(new ResponseEntity<>(visitMapper.toVisitDto(currentVisit), HttpStatus.NO_CONTENT));
    }

    public void deleteVisit(
            @PathVariable(name = "visitId") Integer visitId,
            VisitRepository visitRepository,
            ObjectResponse<ResponseEntity<VisitDto>> response) {
        Visit visit = visitRepository.findById(visitId);
        if (visit == null) {
            response.send(ResponseEntity.notFound().build());
            return;
        }
        visitRepository.delete(visit);
        response.send(new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }
}
