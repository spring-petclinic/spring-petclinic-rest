package org.springframework.samples.petclinic.rest.controller.v1;

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
    public ResponseEntity<String> failingRequest() {
        return ResponseEntity.ok("This endpoint always fails, by design, to demonstrate the error response shape.");
    }
}
