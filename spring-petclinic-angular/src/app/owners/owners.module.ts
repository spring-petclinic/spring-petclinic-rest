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
import {OwnerService} from './owner.service';
import {OwnerListComponent} from './owner-list/owner-list.component';
import {OwnerDetailComponent} from './owner-detail/owner-detail.component';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {OwnerAddComponent} from './owner-add/owner-add.component';
import {OwnerEditComponent} from './owner-edit/owner-edit.component';
import {OwnersRoutingModule} from './owners-routing.module';
import {PetsModule} from '../pets/pets.module';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    OwnersRoutingModule,
    PetsModule
  ],
  declarations: [
    OwnerListComponent,
    OwnerDetailComponent,
    OwnerEditComponent,
    OwnerAddComponent
  ],
  providers: [OwnerService]

})

export class OwnersModule {
}
