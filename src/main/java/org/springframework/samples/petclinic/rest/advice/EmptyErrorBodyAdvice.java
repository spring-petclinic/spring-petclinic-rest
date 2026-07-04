package org.springframework.samples.petclinic.rest.advice;

import java.net.URI;
import java.time.Instant;
import java.util.List;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.samples.petclinic.rest.dto.ValidationMessageDto;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * Ensures every error response (status 400 and above) that a
 * controller returns without an explicit body is still populated
 * with a standard {@link ProblemDetail} payload, matching the
 * API contract for error responses.
 */
@ControllerAdvice
public class EmptyErrorBodyAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(
            MethodParameter returnType,
            Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response) {

        if (body != null) {
            return body;
        }

        if (!(response instanceof ServletServerHttpResponse servletResponse)) {
            return body;
        }

        int statusCode = servletResponse.getServletResponse().getStatus();
        if (statusCode < 400) {
            return body;
        }

        HttpStatus status = HttpStatus.resolve(statusCode);
        if (status == null) {
            return body;
        }

        response.getHeaders().setContentType(
            MediaType.valueOf("application/problem+json")
        );

        String url = request.getURI().toString();

        ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setType(URI.create(url));
        problemDetail.setTitle(status.getReasonPhrase().replace(" ", "") + "Exception");
        problemDetail.setDetail(
            "The requested resource could not be found."
        );
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty(
            "schemaValidationErrors",
            List.<ValidationMessageDto>of()
        );

        return problemDetail;
    }
}
