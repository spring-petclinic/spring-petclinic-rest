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
import {PetTypeService} from './pettype.service';
import {PettypeListComponent} from './pettype-list/pettype-list.component';
import {PettypeAddComponent} from './pettype-add/pettype-add.component';
import {PettypeEditComponent} from './pettype-edit/pettype-edit.component';
import {PettypesRoutingModule} from './pettypes-routing.module';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    PettypesRoutingModule
  ],
  declarations: [
    PettypeListComponent,
    PettypeAddComponent,
    PettypeEditComponent],
  exports: [
    PettypeListComponent
  ],
  providers: [PetTypeService]
})
export class PetTypesModule {
}
