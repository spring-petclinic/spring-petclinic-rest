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

import {Injectable} from '@angular/core';
import {environment} from '../../environments/environment';
import {Observable} from 'rxjs';
import {Pet} from './pet';
import {HttpClient} from '@angular/common/http';
import {catchError} from 'rxjs/operators';
import {HandleError, HttpErrorHandler} from '../error.service';

@Injectable()
export class PetService {

  private entityUrl = environment.REST_API_URL + 'pets';

  private readonly handlerError: HandleError;

  constructor(private http: HttpClient, private httpErrorHandler: HttpErrorHandler) {
    this.handlerError = httpErrorHandler.createHandleError('OwnerService');
  }

  getPets(): Observable<Pet[]> {
    return this.http.get<Pet[]>(this.entityUrl)
      .pipe(
        catchError(this.handlerError('getPets', []))
      );
  }

  getPetById(petId: number): Observable<Pet> {
    return this.http.get<Pet>(this.entityUrl + '/' + petId)
      .pipe(
        catchError(this.handlerError('getPetById', {} as Pet))
      );
  }

  addPet(pet: Pet): Observable<Pet> {
    const ownerId = pet.owner.id;
    const ownersUrl = environment.REST_API_URL + `owners/${ownerId}/pets`;
    return this.http.post<Pet>(ownersUrl, pet)
      .pipe(
        catchError(this.handlerError('addPet', pet))
      );
  }

  updatePet(petId: string, pet: Pet): Observable<Pet> {
    return this.http.put<Pet>(this.entityUrl + '/' + petId, pet)
      .pipe(
        catchError(this.handlerError('updatePet', pet))
      );
  }

  deletePet(petId: string): Observable<number> {
    return this.http.delete<number>(this.entityUrl + '/' + petId)
      .pipe(
        catchError(this.handlerError('deletePet', 0))
      );
  }

}
