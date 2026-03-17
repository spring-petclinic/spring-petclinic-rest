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

import { Component, OnInit } from '@angular/core';
import { OwnerService } from '../owner.service';
import { Owner } from '../owner';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-owner-edit',
  templateUrl: './owner-edit.component.html',
  styleUrls: ['./owner-edit.component.css'],
})
export class OwnerEditComponent implements OnInit {
  owner: Owner;
  errorMessage: string; // server error message
  ownerId: number;
  constructor(
    private ownerService: OwnerService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.owner = {} as Owner;
  }

  ngOnInit() {
    const ownerId = this.route.snapshot.params.id;
    this.ownerService.getOwnerById(ownerId).subscribe(
      (owner) => (this.owner = owner),
      (error) => (this.errorMessage = error as any)
    );
  }

  onSubmit(owner: Owner) {
    const that = this;  
    const ownerId = this.route.snapshot.params.id;
    this.ownerService.updateOwner(ownerId , owner).subscribe(
      (res) => this.gotoOwnerDetail(owner),
      (error) => (this.errorMessage = error as any)
    );
  }

  gotoOwnerDetail(owner: Owner) {
    this.errorMessage = null;
    this.router.navigate(['/owners', owner.id]);
  }
}
