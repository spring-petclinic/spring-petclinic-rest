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
import {Specialty} from './specialty';
import {catchError} from 'rxjs/internal/operators';
import {HandleError, HttpErrorHandler} from '../error.service';
import {HttpClient} from '@angular/common/http';

@Injectable()
export class SpecialtyService {

  private entityUrl = environment.REST_API_URL + 'specialties';

  private handlerError: HandleError;

  constructor(private http: HttpClient, private httpErrorHandler: HttpErrorHandler) {
    this.handlerError = httpErrorHandler.createHandleError('OwnerService');
  }

  getSpecialties(): Observable<Specialty[]> {
    return this.http.get<Specialty[]>(this.entityUrl)
      .pipe(
        catchError(this.handlerError('getSpecialties', []))
      );
  }

  getSpecialtyById(specId: string): Observable<Specialty> {
    return this.http.get<Specialty>((this.entityUrl + '/' + specId))
      .pipe(
        catchError(this.handlerError('getSpecialtyById', {} as Specialty))
      );
  }

  addSpecialty(specialty: Specialty): Observable<Specialty> {
    return this.http.post<Specialty>(this.entityUrl, specialty)
      .pipe(
        catchError(this.handlerError('addSpecialty', specialty))
      );
  }

  updateSpecialty(specId: string, specialty: Specialty): Observable<Specialty> {
    return this.http.put<Specialty>((this.entityUrl + '/' + specId), specialty)
      .pipe(
        catchError(this.handlerError('updateSpecialty', specialty))
      );
  }

  deleteSpecialty(specId: string): Observable<number> {
    return this.http.delete<number>((this.entityUrl + '/' + specId))
      .pipe(
        catchError(this.handlerError('deleteSpecialty', 0))
      );
  }

}
