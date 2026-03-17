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
import {PetType} from '../pettype';
import {PetTypeService} from '../pettype.service';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  selector: 'app-pettype-edit',
  templateUrl: './pettype-edit.component.html',
  styleUrls: ['./pettype-edit.component.css']
})
export class PettypeEditComponent implements OnInit {
  pettype: PetType;
  errorMessage: string;

  constructor(private pettypeService: PetTypeService, private route: ActivatedRoute, private router: Router) {
    this.pettype = {} as PetType;
  }

  ngOnInit() {
    const pettypeId = this.route.snapshot.params.id;
    this.pettypeService.getPetTypeById(pettypeId).subscribe(
      pettype => this.pettype = pettype,
      error => this.errorMessage = error as any);
  }

  onSubmit(pettype: PetType) {
    this.pettypeService.updatePetType(pettype.id.toString(), pettype).subscribe(
      res => {
        console.log('update success');
        this.onBack();
      },
      error => this.errorMessage = error as any);

  }

  onBack() {
    this.router.navigate(['/pettypes']);
  }

}
