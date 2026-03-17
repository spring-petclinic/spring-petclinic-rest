import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {DummyComponent} from './dummy.component';
import {RouterLinkStubDirective, RouterOutletStubComponent} from './router-stubs';

@NgModule({
  imports: [
    CommonModule
  ],
  declarations: [
  DummyComponent,
  RouterLinkStubDirective,
  RouterOutletStubComponent
  ]
})
export class TestingModule { }
