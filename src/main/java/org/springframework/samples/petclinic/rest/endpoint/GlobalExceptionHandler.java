package org.springframework.samples.petclinic.rest.endpoint;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.rest.dto.ValidationMessageDto;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import net.officefloor.plugin.section.clazz.Parameter;
import net.officefloor.web.ObjectResponse;

public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String ERROR_UNEXPECTED = "An unexpected error occurred while processing your request";
    private static final String ERROR_DATA_INTEGRITY = "The requested resource could not be processed due to a data constraint violation";
    private static final String ERROR_INVALID_REQUEST = "The request contains invalid or missing parameters";

    private ProblemDetail detailBuild(Exception e, HttpStatus status, String detail) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setTitle(e.getClass().getSimpleName());
        problemDetail.setDetail(detail);
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("schemaValidationErrors", List.<ValidationMessageDto>of());
        return problemDetail;
    }

    public void handleGeneralException(
            @Parameter Exception ex,
            ObjectResponse<ResponseEntity<ProblemDetail>> response) {
        logger.error("Unexpected error", ex);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ProblemDetail detail = detailBuild(ex, status, ERROR_UNEXPECTED);
        response.send(ResponseEntity.status(status).body(detail));
    }

    public void handleDataIntegrityViolationException(
            @Parameter DataIntegrityViolationException ex,
            ObjectResponse<ResponseEntity<ProblemDetail>> response) {
        logger.warn("Data integrity violation: {}", ex.getMessage());
        logger.debug("Data integrity violation stacktrace", ex);
        HttpStatus status = HttpStatus.NOT_FOUND;
        ProblemDetail detail = detailBuild(ex, status, ERROR_DATA_INTEGRITY);
        response.send(ResponseEntity.status(status).body(detail));
    }

    public void handleMethodArgumentNotValidException(
            @Parameter MethodArgumentNotValidException ex,
            ObjectResponse<ResponseEntity<ProblemDetail>> response) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        BindingResult bindingResult = ex.getBindingResult();
        ProblemDetail detail = detailBuild(ex, status, ERROR_INVALID_REQUEST);
        if (bindingResult.hasErrors()) {
            List<ValidationMessageDto> schemaValidationErrors = bindingResult.getFieldErrors().stream()
                    .map(fieldError -> {
                        String rejectedValue = Objects.toString(fieldError.getRejectedValue(), "null");
                        String defaultMessage = Objects.toString(fieldError.getDefaultMessage(), "Validation failed");
                        String message = "Field '%s' %s (rejected value: %s)".formatted(
                                fieldError.getField(), defaultMessage, rejectedValue);
                        return new ValidationMessageDto(message)
                                .putAdditionalProperty("field", fieldError.getField())
                                .putAdditionalProperty("rejectedValue", rejectedValue)
                                .putAdditionalProperty("defaultMessage", defaultMessage);
                    })
                    .toList();
            logger.debug("Validation errors: {}", bindingResult.getFieldErrors());
            detail.setProperty("schemaValidationErrors", schemaValidationErrors);
        }
        response.send(ResponseEntity.status(status).body(detail));
    }
}
