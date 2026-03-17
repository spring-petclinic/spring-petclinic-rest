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



// export for convenience.
export {ActivatedRoute, Router, RouterLink, RouterOutlet} from '@angular/router';

import {Component, Directive, HostListener, Injectable, Input} from '@angular/core';
import {NavigationExtras} from '@angular/router';
// Only implements params and part of snapshot.params
import {BehaviorSubject} from 'rxjs';

@Directive({
  selector: '[appRouterLink]',
})
export class RouterLinkStubDirective {
  @Input() linkParams: any;
  navigatedTo: any = null;

  @HostListener('click', ['$event'])
  onClick() {
    this.navigatedTo = this.linkParams;
  }
}

@Component({selector: 'app-router-outlet', template: ''})
export class RouterOutletStubComponent {
}

@Injectable()
export class RouterStub {
  navigate(commands: any[], extras?: NavigationExtras) {
  }
}


@Injectable()
export class ActivatedRouteStub {

  // ActivatedRoute.params is Observable
  private subject = new BehaviorSubject(this.testParams);
  params = this.subject.asObservable();

  // Test parameters
  // tslint:disable-next-line:variable-name
  private _testParams: {};
  get testParams() {
    return this._testParams;
  }

  set testParams(params: {}) {
    this._testParams = params;
    this.subject.next(params);
  }

  // ActivatedRoute.snapshot.params
  get snapshot() {
    this.testParams = {id: 1};
    return {params: this.testParams};
  }
}
