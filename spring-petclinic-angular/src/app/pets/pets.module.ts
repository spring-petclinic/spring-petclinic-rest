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

import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {PetsRoutingModule} from './pets-routing.module';
import {PetListComponent} from './pet-list/pet-list.component';
import {PetService} from './pet.service';
import {VisitsModule} from '../visits/visits.module';
import {PetEditComponent} from './pet-edit/pet-edit.component';
import {FormsModule} from '@angular/forms';
import {PetAddComponent} from './pet-add/pet-add.component';

import {MatMomentDateModule, MomentDateAdapter} from '@angular/material-moment-adapter';
import { MatDatepickerModule } from '@angular/material/datepicker';
import {DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE} from '@angular/material/core';

export const MY_DATE_FORMATS = {
  parse: {
    dateInput: 'YYYY/MM/DD',
  },
  display: {
    dateInput: 'YYYY/MM/DD',
    monthYearLabel: 'MM YYYY',
    dateA11yLabel: 'YYYY/MM/DD',
    monthYearA11yLabel: 'MM YYYY',
  },
};


@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    MatDatepickerModule,
    MatMomentDateModule,
    PetsRoutingModule,
    VisitsModule
  ],
  declarations: [
    PetListComponent,
    PetEditComponent,
    PetAddComponent
  ],
  exports: [
    PetListComponent,
    PetEditComponent,
    PetAddComponent
  ],
  providers: [
    PetService,
    {provide: DateAdapter, useClass: MomentDateAdapter, deps: [MAT_DATE_LOCALE]},
    {provide: MAT_DATE_FORMATS, useValue: MY_DATE_FORMATS}
  ]
})
export class PetsModule {
}


