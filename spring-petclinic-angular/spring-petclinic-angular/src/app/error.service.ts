/*
 *
 *  * Copyright 2016-2018 the original author or authors.
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
 * @author Antoine Rey
 */

import {Injectable} from '@angular/core';
import {HttpErrorResponse} from '@angular/common/http';

import {Observable, throwError} from 'rxjs';

/** Type of the handleError function returned by HttpErrorHandler.createHandleError */
export type HandleError =
  <T> (operation?: string, result?: T) => (error: HttpErrorResponse) => Observable<T>;

/** Handles HttpClient errors */
@Injectable()
export class HttpErrorHandler {

  /** Create curried handleError function that already knows the service name */
  createHandleError = (serviceName = '') => <T>
  (operation = 'operation', result = {} as T) => this.handleError(serviceName, operation, result)

  /**
   * Returns a function that handles Http operation failures.
   * @param serviceName = name of the data service that attempted the operation
   * @param operation - name of the operation that failed
   * @param result - type of a valid result
   */
  handleError<T>(serviceName = '', operation = 'operation', result = {} as T) {

    return (error: HttpErrorResponse): Observable<T> => {

      let message = (error.error instanceof ErrorEvent) ?
        error.error.message :
        `server returned code ${error.status} with body "${error.error}"`;
      const errorsHeader = error.headers.get('errors');
      if (errorsHeader) {
        const errors = JSON.parse(errorsHeader);
        // Retrieve the Spring MVC errorMessage of the first FieldError
        if ((errors instanceof Array) && (errors.length > 0) && errors[0].errorMessage) {
          message = errors[0].errorMessage;
        }
      }

      console.error(error);
      console.error(`${serviceName}::${operation} failed: ${message}`);

      return throwError(message);
    };

  }
}
