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
import {PetType} from '../../pettypes/pettype';
import {Owner} from '../../owners/owner';
import {ActivatedRoute, Router} from '@angular/router';
import {PetTypeService} from '../../pettypes/pettype.service';
import {PetService} from '../pet.service';
import {OwnerService} from '../../owners/owner.service';

import * as moment from 'moment';

@Component({
  selector: 'app-pet-add',
  templateUrl: './pet-add.component.html',
  styleUrls: ['./pet-add.component.css']
})
export class PetAddComponent implements OnInit {
  pet: Pet;
  @Input() currentType: PetType;
  currentOwner: Owner;
  petTypes: PetType[];
  addedSuccess = false;
  errorMessage: string;

  constructor(private ownerService: OwnerService, private petService: PetService,
              private petTypeService: PetTypeService, private router: Router, private route: ActivatedRoute) {
    this.pet = {} as Pet;
    this.currentOwner = {} as Owner;
    this.currentType = {} as PetType;
    this.petTypes = [];
  }

  ngOnInit() {
    this.petTypeService.getPetTypes().subscribe(
      pettypes => this.petTypes = pettypes,
      error => this.errorMessage = error as any);

    const ownerId = this.route.snapshot.params.id;
    this.ownerService.getOwnerById(ownerId).subscribe(
      response => {
        this.currentOwner = response;
      },
      error => this.errorMessage = error as any);
  }

  onSubmit(pet: Pet) {
    pet.id = null;
    pet.owner = this.currentOwner;
    // format output from datepicker to short string yyyy-mm-dd format (rfc3339)
    pet.birthDate = moment(pet.birthDate).format('YYYY-MM-DD');
    this.petService.addPet(pet).subscribe(
      newPet => {
        this.pet = newPet;
        this.addedSuccess = true;
        this.gotoOwnerDetail();
      },
      error => this.errorMessage = error as any);
  }

  gotoOwnerDetail() {
    this.router.navigate(['/owners', this.currentOwner.id]);
  }

}
