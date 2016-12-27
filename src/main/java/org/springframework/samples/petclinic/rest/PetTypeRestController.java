package org.springframework.samples.petclinic.rest;

import java.util.ArrayList;
import java.util.Collection;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.service.ext.ClinicServiceExt;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
@RequestMapping("api/pettypes")
public class PetTypeRestController {
	
	@Autowired
	private ClinicServiceExt clinicService;
	
	@RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Collection<PetType>> getAllPetTypes(){
		Collection<PetType> petTypes = new ArrayList<PetType>();
		petTypes.addAll(this.clinicService.findAllPetTypes());
		if (petTypes.isEmpty()){
			return new ResponseEntity<Collection<PetType>>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Collection<PetType>>(petTypes, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/{petTypeId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<PetType> getPetType(@PathVariable("petTypeId") int petTypeId){
		PetType petType = this.clinicService.findPetTypeById(petTypeId);
		if(petType == null){
			return new ResponseEntity<PetType>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<PetType>(petType, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<PetType> addPetType(@RequestBody @Valid PetType petType, BindingResult bindingResult, UriComponentsBuilder ucBuilder){
		BindingErrorsResponse errors = new BindingErrorsResponse();
		HttpHeaders headers = new HttpHeaders();
		if(bindingResult.hasErrors() || (petType == null)){
			errors.addAllErrors(bindingResult);
			headers.add("errors", errors.toJSON());
			return new ResponseEntity<PetType>(headers, HttpStatus.BAD_REQUEST);
		}
		this.clinicService.savePetType(petType);
		headers.setLocation(ucBuilder.path("/api/pettypes/{id}").buildAndExpand(petType.getId()).toUri());
		return new ResponseEntity<PetType>(petType, headers, HttpStatus.CREATED);
	}
	
	@RequestMapping(value = "/{petTypeId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<PetType> updatePetType(@PathVariable("petTypeId") int petTypeId, @RequestBody @Valid PetType petType, BindingResult bindingResult){
		BindingErrorsResponse errors = new BindingErrorsResponse();
		HttpHeaders headers = new HttpHeaders();
		if(bindingResult.hasErrors() || (petType == null)){
			errors.addAllErrors(bindingResult);
			headers.add("errors", errors.toJSON());
			return new ResponseEntity<PetType>(headers, HttpStatus.BAD_REQUEST);
		}
		PetType currentPetType = this.clinicService.findPetTypeById(petTypeId);
		if(currentPetType == null){
			return new ResponseEntity<PetType>(HttpStatus.NOT_FOUND);
		}
		currentPetType.setName(petType.getName());
		this.clinicService.savePetType(currentPetType);
		return new ResponseEntity<PetType>(currentPetType, HttpStatus.NO_CONTENT);
	}
	
	@RequestMapping(value = "/{petTypeId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@Transactional
	public ResponseEntity<Void> deletePetType(@PathVariable("petTypeId") int petTypeId){
		PetType petType = this.clinicService.findPetTypeById(petTypeId);
		if(petType == null){
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		}
		this.clinicService.deletePetType(petType);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

}
