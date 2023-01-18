import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import {PettypeListComponent} from './pettype-list.component';
import {PetTypeService} from '../pettype.service';
import {PetType} from '../pettype';
import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ActivatedRouteStub, RouterStub} from '../../testing/router-stubs';
import {FormsModule} from '@angular/forms';
import {Observable, of} from 'rxjs/index';
import Spy = jasmine.Spy;

class PetTypeServiceStub {
  deletePetType(typeId: string): Observable<number> {
    return of();
  }
  getPetTypes(): Observable<PetType[]> {
    return of();
  }
}


describe('PettypeListComponent', () => {
  let component: PettypeListComponent;
  let fixture: ComponentFixture<PettypeListComponent>;
  let pettypeService: PetTypeService;
  let spy: Spy;
  let testPettypes: PetType[];
  let responseStatus: number;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ PettypeListComponent ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      imports: [FormsModule],
      providers: [
        {provide: PetTypeService, useClass: PetTypeServiceStub},
        {provide: Router, useClass: RouterStub},
        {provide: ActivatedRoute, useClass: ActivatedRouteStub}
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PettypeListComponent);
    component = fixture.componentInstance;

    testPettypes = [{
      id: 1,
      name: 'test'
    }];

    pettypeService = fixture.debugElement.injector.get(PetTypeService);
    responseStatus = 204; // success delete return NO_CONTENT
    component.pettypes = testPettypes;

    spy = spyOn(pettypeService, 'deletePetType')
      .and.returnValue(of(responseStatus));

    fixture.detectChanges();
  });

  it('should create PettypeListComponent', () => {
    expect(component).toBeTruthy();
  });

  it('should call deletePetType() method', () => {
    fixture.detectChanges();
    component.deletePettype(component.pettypes[0]);
    expect(spy.calls.any()).toBe(true, 'deletePetType called');
  });
});
