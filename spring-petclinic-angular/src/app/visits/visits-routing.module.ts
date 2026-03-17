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
import {VisitListComponent} from './visit-list/visit-list.component';
import {VisitEditComponent} from './visit-edit/visit-edit.component';
import {VisitAddComponent} from './visit-add/visit-add.component';

const visitRoutes: Routes = [
  {path: 'visits', component: VisitListComponent},
  {path: 'visits/add', component: VisitAddComponent},
  {path: 'visits/:id/edit', component: VisitEditComponent}

];


@NgModule({
  imports: [
    RouterModule.forChild(visitRoutes)
  ],
  exports: [
    RouterModule
  ]
})
export class VisitsRoutingModule {
}
