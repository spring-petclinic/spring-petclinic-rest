package org.springframework.samples.petclinic.service.clinicService;

import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.ActiveProfiles;

@TestConfiguration
@ActiveProfiles({"spring-data-jpa", "hsqldb"})
public class ApplicationTestConfig {

	public ApplicationTestConfig(){
		MockitoAnnotations.openMocks(this);
	}

}
