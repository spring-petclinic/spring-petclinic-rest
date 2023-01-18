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
import {RouterModule, Routes} from '@angular/router';
import {OwnerDetailComponent} from './owner-detail/owner-detail.component';
import {OwnerListComponent} from './owner-list/owner-list.component';
import {OwnerEditComponent} from './owner-edit/owner-edit.component';
import {OwnerAddComponent} from './owner-add/owner-add.component';
import {PetAddComponent} from '../pets/pet-add/pet-add.component';

const ownerRoutes: Routes = [
  {path: 'owners', component: OwnerListComponent},
  {path: 'owners/add', component: OwnerAddComponent},
  {path: 'owners/:id', component: OwnerDetailComponent},
  {path: 'owners/:id/edit', component: OwnerEditComponent},
  {path: 'owners/:id/pets/add', component: PetAddComponent}
];

@NgModule({
  imports: [RouterModule.forChild(ownerRoutes)],
  exports: [RouterModule]
})

export class OwnersRoutingModule {
}
