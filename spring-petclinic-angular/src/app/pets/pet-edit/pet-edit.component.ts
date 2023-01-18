/*
 *
 *  * Copyright 2016-2017 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */


/**
 * @author Vitaliy Fedoriv
 */

import {Component, Input, OnInit} from '@angular/core';
import {Pet} from '../pet';
import {PetService} from '../pet.service';
import {ActivatedRoute, Router} from '@angular/router';
import {Owner} from '../../owners/owner';
import {PetType} from '../../pettypes/pettype';
import {PetTypeService} from '../../pettypes/pettype.service';

import * as moment from 'moment';
import {OwnerService} from '../../owners/owner.service';

@Component({
  selector: 'app-pet-edit',
  templateUrl: './pet-edit.component.html',
  styleUrls: ['./pet-edit.component.css']
})
export class PetEditComponent implements OnInit {
  pet: Pet;
  @Input() currentType: PetType;
  currentOwner: Owner;
  petTypes: PetType[];
  errorMessage: string;

  constructor(private petService: PetService,
              private petTypeService: PetTypeService,
              private ownerService: OwnerService,
              private router: Router,
              private route: ActivatedRoute) {
    this.pet = {} as Pet;
    this.currentOwner = {} as Owner;
    this.currentType = {} as PetType;
    this.petTypes = [];
  }

  ngOnInit() {

    this.petTypeService.getPetTypes().subscribe(
      pettypes => this.petTypes = pettypes,
      error => this.errorMessage = error as any);

    const petId = this.route.snapshot.params.id;
    this.petService.getPetById(petId).subscribe(
      pet => {
        this.pet = pet;
        this.ownerService.getOwnerById(pet.ownerId).subscribe(
          response => {
            this.currentOwner = response;
          });
        this.currentType = this.pet.type;
      },
      error => this.errorMessage = error as any);

  }

  onSubmit(pet: Pet) {
    pet.type = this.currentType;
    const that = this;
    // format output from datepicker to short string yyyy-mm-dd format (rfc3339)
    pet.birthDate = moment(pet.birthDate).format('YYYY-MM-DD');

    this.petService.updatePet(pet.id.toString(), pet).subscribe(
      res => this.gotoOwnerDetail(this.currentOwner),
      error => this.errorMessage = error as any
    );
  }

  gotoOwnerDetail(owner: Owner) {
    this.router.navigate(['/owners', owner.id]);
  }

}
