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

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.stream.Collectors;

import static java.time.OffsetDateTime.now;
import static org.springframework.http.HttpStatus.*;

/**
 * @author Alexander Dudkin
 */
@ControllerAdvice
public class ExceptionControllerAdvice {

    /**
     * Handles RuntimeException and returns a {@link ProblemDetail} with HTTP 500 status.
     *
     * @param ex      the thrown RuntimeException
     * @param request the current WebRequest
     * @return a ResponseEntity with a ProblemDetail and HTTP 500 status
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(code = INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseEntity<ProblemDetail> handleRuntimeException(RuntimeException ex, WebRequest request) {
        return createProblemDetail(
            "Unexpected error",
            INTERNAL_SERVER_ERROR.value(),
            ex.getLocalizedMessage(),
            request,
            now()
        );
    }

    /**
     * Handles DataIntegrityViolationException and returns a {@link ProblemDetail} with HTTP 404 status.
     *
     * @param ex      the thrown DataIntegrityViolationException
     * @param request the current WebRequest
     * @return a ResponseEntity with a ProblemDetail and HTTP 404 status
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(code = NOT_FOUND)
    @ResponseBody
    public ResponseEntity<?> handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request) {
        return createProblemDetail(
            "Data Integrity Violation",
            NOT_FOUND.value(),
            ex.getLocalizedMessage(),
            request,
            now()
        );
    }

    /**
     * Handles MethodArgumentNotValidException and returns a {@link ProblemDetail} with HTTP 400 status.
     * Aggregates all validation error messages into a single detail string.
     *
     * @param ex      the thrown MethodArgumentNotValidException
     * @param request the current WebRequest
     * @return a ResponseEntity with a ProblemDetail and HTTP 400 status
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        String detail = ex.getBindingResult().getAllErrors().stream()
            .map(this::formatErrorMessage)
            .collect(Collectors.joining("; "));

        return createProblemDetail(
            "Validation Error",
            BAD_REQUEST.value(),
            detail,
            request,
            now()
        );
    }

    /**
     * Formats the error message from an ObjectError.
     *
     * @param error the ObjectError to format
     * @return a formatted error message
     */
    private String formatErrorMessage(ObjectError error) {
        return (error instanceof FieldError fieldError)
            ? String.format("Field '%s' %s", fieldError.getField(), fieldError.getDefaultMessage())
            : error.getDefaultMessage();
    }

    /**
     * Creates a ProblemDetail object with the provided details.
     *
     * @param title     the title of the problem
     * @param status    the HTTP status code
     * @param detail    the detail message of the problem
     * @param request   the current WebRequest
     * @param timestamp the timestamp of the problem occurrence
     * @return a ResponseEntity with the ProblemDetail and the specified status
     */
    private ResponseEntity<ProblemDetail> createProblemDetail(String title, int status, String detail, WebRequest request, OffsetDateTime timestamp) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setTitle(title);
        problemDetail.setDetail(detail);
        problemDetail.setInstance(URI.create(request.getDescription(false).substring(4)));
        problemDetail.setProperty("timestamp", timestamp);

        return new ResponseEntity<>(problemDetail, HttpStatus.valueOf(status));
    }
}
