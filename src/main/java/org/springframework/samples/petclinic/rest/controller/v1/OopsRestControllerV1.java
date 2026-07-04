package org.springframework.samples.petclinic.rest.controller.v1;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
@RequestMapping("api")
public class OopsRestControllerV1 {

    @GetMapping("/oops")
    public ResponseEntity<ProblemDetail> failingRequest() {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "This endpoint always fails, by design, to demonstrate the error response shape."
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(detail);
    }
}
