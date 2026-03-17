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
import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';

import {SpecialtyListComponent} from './specialty-list.component';
import {FormsModule} from '@angular/forms';
import {SpecialtyService} from '../specialty.service';
import {Specialty} from '../specialty';
import {ActivatedRoute, Router} from '@angular/router';
import {ActivatedRouteStub, RouterStub} from '../../testing/router-stubs';
import {Observable, of} from 'rxjs/index';
import Spy = jasmine.Spy;

class SpecialityServiceStub {
  deleteSpecialty(specId: string): Observable<number> {
    return of();
  }
  getSpecialties(): Observable<Specialty[]> {
    return of();
  }
}


describe('SpecialtyListComponent', () => {
  let component: SpecialtyListComponent;
  let fixture: ComponentFixture<SpecialtyListComponent>;
  let specialtyService: SpecialtyService;
  let spy: Spy;
  let testSpecialties: Specialty[];
  let responseStatus: number;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [SpecialtyListComponent],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      imports: [FormsModule],
      providers: [
        {provide: SpecialtyService, useClass: SpecialityServiceStub},
        {provide: Router, useClass: RouterStub},
        {provide: ActivatedRoute, useClass: ActivatedRouteStub}
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SpecialtyListComponent);
    component = fixture.componentInstance;
    testSpecialties = [{
      id: 1,
      name: 'test'
    }];

    specialtyService = fixture.debugElement.injector.get(SpecialtyService);
    responseStatus = 204; // success delete return NO_CONTENT
    component.specialties = testSpecialties;

    spy = spyOn(specialtyService, 'deleteSpecialty')
      .and.returnValue(of(responseStatus));

    fixture.detectChanges();
  });

  it('should create SpecialtyListComponent', () => {
    expect(component).toBeTruthy();
  });

  it('should call deleteSpecialty() method', () => {
    fixture.detectChanges();
    component.deleteSpecialty(component.specialties[0]);
    expect(spy.calls.any()).toBe(true, 'deleteSpecialty called');
  });

});
