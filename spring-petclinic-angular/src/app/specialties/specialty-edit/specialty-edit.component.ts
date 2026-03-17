/*
 *
 *  * Copyright 2017-2018 the original author or authors.
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
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  selector: 'app-specialty-edit',
  templateUrl: './specialty-edit.component.html',
  styleUrls: ['./specialty-edit.component.css']
})
export class SpecialtyEditComponent implements OnInit {
  specialty: Specialty;
  errorMessage: string;

  constructor(private specialtyService: SpecialtyService, private route: ActivatedRoute, private router: Router) {
    this.specialty = {} as Specialty;
  }

  ngOnInit() {
    const specId = this.route.snapshot.params.id;
    this.specialtyService.getSpecialtyById(specId).subscribe(
      specialty => this.specialty = specialty,
      error => this.errorMessage = error as any);
  }

  onSubmit(specialty: Specialty) {
    this.specialtyService.updateSpecialty(specialty.id.toString(), specialty).subscribe(
      res => {
        console.log('update success');
        this.onBack();
      },
      error => this.errorMessage = error as any);
 }

  onBack() {
    this.router.navigate(['/specialties']);
  }

}
