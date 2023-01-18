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


import { SpringPetclinicAngularPage } from './app.po';

describe('spring-petclinic-angular App', () => {
  let page: SpringPetclinicAngularPage;

  beforeEach(() => {
    page = new SpringPetclinicAngularPage();
  });

  it('should display app works message', done => {
    page.navigateTo();
    // page.getParagraphText()
    //   .then(msg => expect(msg).toEqual('app works!'))
    //   .then(done, done.fail);
    expect(page.getParagraphText()).toEqual('app works!');
  });
});
