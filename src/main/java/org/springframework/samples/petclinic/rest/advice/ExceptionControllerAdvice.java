/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.samples.petclinic.rest.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.rest.controller.BindingErrorsResponse;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * Global Exception handler for REST controllers.
 * <p>
 * This class handles exceptions thrown by REST controllers and returns
 * appropriate HTTP responses to the client.
 *
 * @author Vitaliy Fedoriv
 * @author Alexander Dudkin
 */
@ControllerAdvice
public class ExceptionControllerAdvice {

    /**
     * Record for storing error information.
     * <p>
     * This record encapsulates the class name and message of the exception.
     *
     * @param className The name of the exception class
     * @param exMessage The message of the exception
     */
    private record ErrorInfo(String className, String exMessage) {
        public ErrorInfo(Exception ex) {
            this(ex.getClass().getName(), ex.getLocalizedMessage());
        }
    }

    /**
     * Handles all general exceptions by returning a 500 Internal Server Error status with error details.
     *
     * @param  e The exception to be handled
     * @return A {@link ResponseEntity} containing the error information and a 500 Internal Server Error status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorInfo> handleGeneralException(Exception e) {
        ErrorInfo info = new ErrorInfo(e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(info);
    }

    /**
     * Handles {@link DataIntegrityViolationException} which typically indicates database constraint violations.
     * This method returns a 404 Not Found status if an entity does not exist.
     *
     * @param ex The {@link DataIntegrityViolationException} to be handled
     * @return A {@link ResponseEntity} containing the error information and a 404 Not Found status
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @ResponseBody
    public ResponseEntity<ErrorInfo> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        ErrorInfo errorInfo = new ErrorInfo(ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorInfo);
    }

    /**
     * Handles exception thrown by Bean Validation on controller methods parameters
     *
     * @param ex      The thrown exception
     *
     * @return an empty response entity
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<ErrorInfo> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        BindingErrorsResponse errors = new BindingErrorsResponse();
        BindingResult bindingResult = ex.getBindingResult();
        if (bindingResult.hasErrors()) {
            errors.addAllErrors(bindingResult);
            return ResponseEntity.badRequest().body(new ErrorInfo("MethodArgumentNotValidException", "Validation failed"));
        }
        return ResponseEntity.badRequest().build();
    }

}
