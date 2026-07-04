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
package org.springframework.samples.petclinic.rest.controller.v1;

import jakarta.validation.ConstraintViolationException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.mapper.SpecialtyMapper;
import org.springframework.samples.petclinic.model.Specialty;
import org.springframework.samples.petclinic.rest.api.SpecialtiesApi;
import org.springframework.samples.petclinic.rest.dto.SpecialtyDto;
import org.springframework.samples.petclinic.rest.dto.SpecialtyFieldsDto;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Vitaliy Fedoriv
 */
@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
@RequestMapping("api")
public class SpecialtyRestControllerV1 implements SpecialtiesApi {

    private final ClinicService clinicService;
    private final SpecialtyMapper specialtyMapper;

    public SpecialtyRestControllerV1(
            ClinicService clinicService,
            SpecialtyMapper specialtyMapper) {
        this.clinicService = clinicService;
        this.specialtyMapper = specialtyMapper;
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @Override
    public ResponseEntity<List<SpecialtyDto>> listSpecialties(String ifNoneMatch) {
        List<SpecialtyDto> specialties = new ArrayList<>();
        specialties.addAll(
            specialtyMapper.toSpecialtyDtos(
                this.clinicService.findAllSpecialties()
            )
        );

        if (specialties.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(specialties, HttpStatus.OK);
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @Override
    public ResponseEntity<SpecialtyDto> getSpecialty(Integer specialtyId, String ifNoneMatch) {
        Specialty specialty =
            this.clinicService.findSpecialtyById(specialtyId);

        if (specialty == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        String eTag = "\"" + Objects.hash(
            specialty.getId(),
            specialty.getName()
        ) + "\"";

        if (eTag.equals(ifNoneMatch)) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
                .eTag(eTag)
                .build();
        }

        return ResponseEntity.ok()
            .eTag(eTag)
            .body(specialtyMapper.toSpecialtyDto(specialty));
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @Override
    public ResponseEntity<SpecialtyDto> addSpecialty(
            SpecialtyFieldsDto specialtyDto) {

        HttpHeaders headers = new HttpHeaders();

        Specialty specialty =
            specialtyMapper.toSpecialty(specialtyDto);

        this.clinicService.saveSpecialty(specialty);

        headers.setLocation(
            UriComponentsBuilder
                .newInstance()
                .path("/api/specialties/{id}")
                .buildAndExpand(specialty.getId())
                .toUri()
        );

        return new ResponseEntity<>(
            specialtyMapper.toSpecialtyDto(specialty),
            headers,
            HttpStatus.CREATED
        );
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @Override
    public ResponseEntity<Void> updateSpecialty(
            Integer specialtyId,
            SpecialtyDto specialtyDto) {

        if (specialtyDto.getId() == null) {
            throw new ConstraintViolationException(
                "Specialty id must not be null",
                java.util.Set.of()
            );
        }

        Specialty currentSpecialty =
            this.clinicService.findSpecialtyById(specialtyId);

        if (currentSpecialty == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        currentSpecialty.setName(specialtyDto.getName());

        this.clinicService.saveSpecialty(currentSpecialty);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @Transactional
    @Override
    public ResponseEntity<SpecialtyDto> deleteSpecialty(
            Integer specialtyId) {

        Specialty specialty =
            this.clinicService.findSpecialtyById(specialtyId);

        if (specialty == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        SpecialtyDto deletedSpecialtyDto = specialtyMapper.toSpecialtyDto(specialty);
        this.clinicService.deleteSpecialty(specialty);

        return new ResponseEntity<>(deletedSpecialtyDto, HttpStatus.OK);
    }
}
