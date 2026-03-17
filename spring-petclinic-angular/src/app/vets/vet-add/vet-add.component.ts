/*
 *
 *  * Copyright 2016-2018 the original author or authors.
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

import {Component, OnInit} from '@angular/core';
import {Specialty} from '../../specialties/specialty';
import {SpecialtyService} from 'app/specialties/specialty.service';
import {Vet} from '../vet';
import {Router} from '@angular/router';
import {VetService} from '../vet.service';

@Component({
  selector: 'app-vet-add',
  templateUrl: './vet-add.component.html',
  styleUrls: ['./vet-add.component.css']
})
export class VetAddComponent implements OnInit {
  vet: Vet;
  specialtiesList: Specialty[];
  selectedSpecialty: Specialty;
  errorMessage: string;

  constructor(private specialtyService: SpecialtyService, private vetService: VetService, private router: Router) {
    this.vet = {} as Vet;
    this.selectedSpecialty = {} as Specialty;
    this.specialtiesList = [];
  }

  ngOnInit() {
    this.specialtyService.getSpecialties().subscribe(
      specialties => this.specialtiesList = specialties,
      error => this.errorMessage = error as any
    );
  }

  onSubmit(vet: Vet) {
    vet.id = null;
    vet.specialties = [];
    if (this.selectedSpecialty.id !== undefined) {
      vet.specialties.push(this.selectedSpecialty);
    }
    this.vetService.addVet(vet).subscribe(
      newVet => {
        this.vet = newVet;
        this.gotoVetList();
      },
      error => this.errorMessage = error as any
    );
  }

  gotoVetList() {
    this.router.navigate(['/vets']);
  }
}
