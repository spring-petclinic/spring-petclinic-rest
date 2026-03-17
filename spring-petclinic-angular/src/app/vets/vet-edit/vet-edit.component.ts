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
import {Vet} from '../vet';
import {VetService} from '../vet.service';
import {ActivatedRoute, Router} from '@angular/router';
import {SpecialtyService} from '../../specialties/specialty.service';
import {Specialty} from '../../specialties/specialty';
import {FormBuilder, FormGroup, FormControl, Validators} from '@angular/forms';

@Component({
  selector: 'app-vet-edit',
  templateUrl: './vet-edit.component.html',
  styleUrls: ['./vet-edit.component.css']
})
export class VetEditComponent implements OnInit {
  vetEditForm: FormGroup;
  idCtrl: FormControl;
  firstNameCtrl: FormControl;
  lastNameCtrl: FormControl;
  specialtiesCtrl: FormControl;
  vet: Vet;
  specList: Specialty[];
  errorMessage: string;

  constructor(private formBuilder: FormBuilder, private specialtyService: SpecialtyService,
              private vetService: VetService, private route: ActivatedRoute, private router: Router) {
    this.vet = {} as Vet;
    this.specList = [] as Specialty[];
    this.buildForm();
  }

  buildForm() {
this.idCtrl = new FormControl(null);
    this.firstNameCtrl = new FormControl('', [Validators.required, Validators.minLength(2)]);
    this.lastNameCtrl = new FormControl('', [Validators.required, Validators.minLength(2)]);
    this.specialtiesCtrl = new FormControl(null);
    this.vetEditForm = this.formBuilder.group({
      id: this.idCtrl,
      firstName: this.firstNameCtrl,
      lastName: this.lastNameCtrl,
      specialties: this.specialtiesCtrl
    });
  }

  compareSpecFn(c1: Specialty, c2: Specialty): boolean {
    return c1 && c2 ? c1.id === c2.id : c1 === c2;
  }

  initFormValues() {
    this.idCtrl.setValue(this.vet.id);
    this.firstNameCtrl.setValue(this.vet.firstName);
    this.lastNameCtrl.setValue(this.vet.lastName);
    this.specialtiesCtrl.setValue(this.vet.specialties);
  }

  ngOnInit() {
    // we use SpecResolver and VetResolver (get data before init component)
    this.specList = this.route.snapshot.data.specs;
    this.vet = this.route.snapshot.data.vet;
    this.vet.specialties = this.route.snapshot.data.vet.specialties;
    this.initFormValues();
  }

  onSubmit(vet: Vet) {
    this.vetService.updateVet(vet.id.toString(), vet).subscribe(
      res => {
        console.log('update success');
        this.gotoVetList();
      },
      error => this.errorMessage = error as any);

  }

  gotoVetList() {
    this.router.navigate(['/vets']);
  }

}
