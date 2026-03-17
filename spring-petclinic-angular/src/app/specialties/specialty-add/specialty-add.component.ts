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

import {
  Component,
  EventEmitter,
  OnInit,
  Output,
  ViewChild,
} from '@angular/core';
import { NgForm } from '@angular/forms';
import { Specialty } from '../specialty';
import { SpecialtyService } from '../specialty.service';

@Component({
  selector: 'app-specialty-add',
  templateUrl: './specialty-add.component.html',
  styleUrls: ['./specialty-add.component.css'],
})
export class SpecialtyAddComponent implements OnInit {
  @ViewChild('specialityForm', { static: true }) specialityForm: NgForm;
  speciality: Specialty;
  addedSuccess = false;
  errorMessage: string;
  @Output() newSpeciality = new EventEmitter<Specialty>();

  constructor(private specialtyService: SpecialtyService) {
    this.speciality = {} as Specialty;
  }

  ngOnInit() {}

  onSubmit(specialty: Specialty) {
    this.specialtyService.addSpecialty(specialty).subscribe(
      (newSpecialty) => {
        this.speciality = newSpecialty;
        this.addedSuccess = true;
        this.newSpeciality.emit(this.speciality);
      },
      (error) => (this.errorMessage = error as any)
    );
  }
}
