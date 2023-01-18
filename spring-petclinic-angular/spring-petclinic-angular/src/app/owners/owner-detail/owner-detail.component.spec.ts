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

/* tslint:disable:no-unused-variable */

/**
 * @author Vitaliy Fedoriv
 */

import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA, DebugElement } from '@angular/core';
import { By } from '@angular/platform-browser';
import { ENGINE_METHOD_PKEY_ASN1_METHS } from 'constants';
import { OwnerDetailComponent } from './owner-detail.component';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { OwnerService } from '../owner.service';
import { ActivatedRoute, Router } from '@angular/router';
import { ActivatedRouteStub, RouterStub } from '../../testing/router-stubs';
import { Owner } from '../owner';
import { Observable, of } from 'rxjs';

class OwnerServiceStub {
  getOwnerById(): Observable<Owner> {
    return of({ id: 1, firstName: 'James', lastName: 'Franklin'  } as Owner);
  }
}

describe('OwnerDetailComponent', () => {
  let component: OwnerDetailComponent;
  let fixture: ComponentFixture<OwnerDetailComponent>;
  let ownerService = new OwnerServiceStub();
  let de: DebugElement;
  let el: HTMLElement;
  let router: Router;
  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [OwnerDetailComponent],
        schemas: [CUSTOM_ELEMENTS_SCHEMA],
        imports: [FormsModule, RouterTestingModule],
        providers: [
          { provide: OwnerService, useClass: OwnerServiceStub },
          { provide: Router, useClass: RouterStub },
          { provide: ActivatedRoute, useClass: ActivatedRouteStub },
        ],
      }).compileComponents();
    })
  );
  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [OwnerDetailComponent],
        schemas: [CUSTOM_ELEMENTS_SCHEMA],
        imports: [FormsModule, RouterTestingModule],
        providers: [
          { provide: OwnerService, useValue: ownerService },
          { provide: Router, useClass: RouterStub },
          { provide: ActivatedRoute, useClass: ActivatedRouteStub },
        ],
      }).compileComponents();
    })
  );

  const owner: Owner = {
    id: 10,
    firstName: 'James',
    lastName: 'Franklin',
    address: '110 W. Liberty St.',
    city: 'Madison',
    telephone: '6085551023',
    pets: null,
  };

  beforeEach(() => {
    fixture = TestBed.createComponent(OwnerDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.get(Router);
  });

  it('should create OwnerDetailComponent', () => {
    expect(component).toBeTruthy();
  });

  it('find owner using ownerId', () => {
    fixture.detectChanges();
    fixture.whenStable().then(() => {
      // wait for async getOwners
      fixture.detectChanges(); // update view with name
      de = fixture.debugElement.query(By.css('.ownerFullName'));
      el = de.nativeElement;
      expect(el.innerText).toBe(
        owner.firstName.toString() + ' ' + owner.lastName.toString()
      );
    });
  });

  it('routing to owners page on click of editOwner,addPet,gotoOwnersList', () => {
    spyOn(router, 'navigate');
    let buttons = fixture.debugElement.queryAll(By.css('button'));

    let ownersListButton = buttons[0].nativeElement;
    ownersListButton.click();
    spyOn(component, 'gotoOwnersList').and.callThrough();
    expect(router.navigate).toHaveBeenCalledWith(['/owners']);

    let editOwnerButton = buttons[1].nativeElement;
    editOwnerButton.click();
    spyOn(component, 'editOwner').and.callThrough();
    expect(router.navigate).toHaveBeenCalledWith(['/owners']);

    let addNewPetButton = buttons[2].nativeElement;
    addNewPetButton.click();
    spyOn(component, 'addPet').and.callThrough();
    expect(router.navigate).toHaveBeenCalledWith(['/owners']);
  });

});
