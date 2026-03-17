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

import {
  async,
  ComponentFixture,
  TestBed,
  waitForAsync,
} from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { OwnerEditComponent } from './owner-edit.component';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { OwnerService } from '../owner.service';
import { ActivatedRoute, Router } from '@angular/router';
import { ActivatedRouteStub, RouterStub } from '../../testing/router-stubs';
import { Owner } from '../owner';
import { Observable, of } from 'rxjs';
import { By } from '@angular/platform-browser';
import { OwnerListComponent } from '../owner-list/owner-list.component';

class OwnserServiceStub {
  getOwnerById(): Observable<Owner> {
    return of({ id: 1, firstName: 'James' } as Owner);
  }
}

describe('OwnerEditComponent', () => {
  let component: OwnerEditComponent;
  let fixture: ComponentFixture<OwnerEditComponent>;
  let router: Router;
  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [OwnerEditComponent],
        schemas: [CUSTOM_ELEMENTS_SCHEMA],
        // schemas: [ NO_ERRORS_SCHEMA ],
        imports: [FormsModule, RouterTestingModule.withRoutes([
          { path: 'owners', component: OwnerListComponent}
      ])],
        providers: [
          { provide: OwnerService, useClass: OwnserServiceStub },
          { provide: Router, useClass: RouterStub },
          { provide: ActivatedRoute, useClass: ActivatedRouteStub },
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(OwnerEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router=TestBed.get(Router);
    spyOn(router,'navigate');
  });

  it('should create OwnerEditComponent', () => {
    expect(component).toBeTruthy();
  });

  it('back button routing', async() => {
    let buttons = fixture.debugElement.queryAll(By.css('button'));
    let backbutton = buttons[0];
    backbutton.triggerEventHandler('click', null);
    spyOn(component, 'gotoOwnerDetail').and.callThrough();
    expect(router.navigate).toHaveBeenCalledWith(['/owners', 1]);
  });

 
  it('update owner', async(() => {
    let buttons = fixture.debugElement.queryAll(By.css('button'));
    let updateOwnerButton = buttons[1].nativeElement;
    spyOn(component, 'onSubmit');
    updateOwnerButton.click();
    expect(component.onSubmit).toHaveBeenCalled();
  }));

});
