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
import {Specialty} from '../specialty';
import {SpecialtyService} from '../specialty.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-specialty-list',
  templateUrl: './specialty-list.component.html',
  styleUrls: ['./specialty-list.component.css']
})
export class SpecialtyListComponent implements OnInit {
  specialties: Specialty[];
  errorMessage: string;
  responseStatus: number;
  isInsert = false;

  constructor(private specService: SpecialtyService, private router: Router) {
    this.specialties = [];
  }

  ngOnInit() {
    this.specService.getSpecialties().subscribe(
      specialties => this.specialties = specialties,
      error => this.errorMessage = error as any);
  }

  deleteSpecialty(specialty: Specialty) {
    this.specService.deleteSpecialty(specialty.id.toString()).subscribe(
      response => {
        this.responseStatus = response;
        this.specialties = this.specialties.filter(currentItem => !(currentItem.id === specialty.id));
      },
      error => this.errorMessage = error as any);
  }

  onNewSpecialty(newSpecialty: Specialty) {
    this.specialties.push(newSpecialty);
    this.showAddSpecialtyComponent();
  }

  showAddSpecialtyComponent() {
    this.isInsert = !this.isInsert;
  }

  showEditSpecialtyComponent(updatedSpecialty: Specialty) {
    this.router.navigate(['/specialties', updatedSpecialty.id.toString(), 'edit']);
  }

  gotoHome() {
    this.router.navigate(['/welcome']);
  }

}
