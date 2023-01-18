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
import {PetType} from './pettype';
import {HttpClient} from '@angular/common/http';
import {catchError} from 'rxjs/internal/operators';
import {HandleError, HttpErrorHandler} from '../error.service';

@Injectable()
export class PetTypeService {

  entityUrl = environment.REST_API_URL + 'pettypes';

  private readonly handlerError: HandleError;

  constructor(private http: HttpClient, private httpErrorHandler: HttpErrorHandler) {
    this.handlerError = httpErrorHandler.createHandleError('OwnerService');
  }

  getPetTypes(): Observable<PetType[]> {
    return this.http.get<PetType[]>(this.entityUrl)
      .pipe(
        catchError(this.handlerError('getPetTypes', []))
      );
  }

  getPetTypeById(typeId: string): Observable<PetType> {
    return this.http.get<PetType>((this.entityUrl + '/' + typeId))
      .pipe(
        catchError(this.handlerError('getPetTypeById', {} as PetType))
      );
  }

  updatePetType(typeId: string, petType: PetType): Observable<PetType> {
    return this.http.put<PetType>(this.entityUrl + '/' + typeId, petType)
      .pipe(
        catchError(this.handlerError('updatePetType', petType))
      );
  }

  addPetType(petType: PetType): Observable<PetType> {
    return this.http.post<PetType>(this.entityUrl, petType)
      .pipe(
        catchError(this.handlerError('addPetType', petType))
      );
  }

  deletePetType(typeId: string): Observable<number> {
    return this.http.delete<number>(this.entityUrl + '/' + typeId)
      .pipe(
        catchError(this.handlerError('deletePetType', 0))
      );
  }

}
