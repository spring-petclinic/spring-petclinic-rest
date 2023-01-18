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

import {Component, OnInit} from '@angular/core';
import {OwnerService} from '../owner.service';
import {ActivatedRoute, Router} from '@angular/router';
import {Owner} from '../owner';


@Component({
  selector: 'app-owner-detail',
  templateUrl: './owner-detail.component.html',
  styleUrls: ['./owner-detail.component.css']
})
export class OwnerDetailComponent implements OnInit {
  errorMessage: string;
  owner: Owner;

  constructor(private route: ActivatedRoute, private router: Router, private ownerService: OwnerService) {
    this.owner = {} as Owner;
  }

  ngOnInit() {
    const ownerId = this.route.snapshot.params.id;
    this.ownerService.getOwnerById(ownerId).subscribe(
      owner => this.owner = owner,
      error => this.errorMessage = error as any);
  }

  gotoOwnersList() {
    this.router.navigate(['/owners']);
  }

  editOwner() {
    this.router.navigate(['/owners', this.owner.id, 'edit']);
  }

  addPet(owner: Owner) {
    this.router.navigate(['/owners', owner.id, 'pets', 'add']);
  }


}
