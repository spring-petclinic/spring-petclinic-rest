package org.springframework.samples.petclinic.rest.controller.v2;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.mapper.OwnerMapper;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.rest.api.OwnerV2Api;
import org.springframework.samples.petclinic.rest.dto.OwnerPageDto;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
@RequestMapping("/api")
public class OwnerRestControllerV2 implements OwnerV2Api {

    private final ClinicService clinicService;
    private final OwnerMapper ownerMapper;

    public OwnerRestControllerV2(ClinicService clinicService, OwnerMapper ownerMapper) {
        this.clinicService = clinicService;
        this.ownerMapper = ownerMapper;
    }

    @Override
    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    public ResponseEntity<OwnerPageDto> listOwnersPage(String lastName, Integer page, Integer size, String ifNoneMatch) {
        int pageNumber = page == null ? 0 : page;
        int pageSize = size == null ? 20 : size;
        Page<Owner> owners = this.clinicService.findOwners(
            lastName,
            PageRequest.of(pageNumber, pageSize, Sort.by("id")));
        return new ResponseEntity<>(ownerMapper.toOwnerPageDto(owners), HttpStatus.OK);
    }
}
