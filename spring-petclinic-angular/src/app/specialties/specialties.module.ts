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
import {FormsModule} from '@angular/forms';
import {SpecialtyService} from './specialty.service';
import {SpecialtyListComponent} from './specialty-list/specialty-list.component';
import {SpecialtiesRoutingModule} from './specialties-routing.module';
import {SpecialtyAddComponent} from './specialty-add/specialty-add.component';
import {SpecialtyEditComponent} from './specialty-edit/specialty-edit.component';
import {SpecResolver} from './spec-resolver';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    SpecialtiesRoutingModule
  ],
  declarations: [
    SpecialtyListComponent,
    SpecialtyAddComponent,
    SpecialtyEditComponent
  ],
  exports: [
    SpecialtyListComponent
  ],
  providers: [SpecialtyService, SpecResolver]
})
export class SpecialtiesModule {
}
