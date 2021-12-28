package org.springframework.samples.petclinic.service.clinicService;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * <p> Integration test using the 'Spring Data' profile.
 *
 * @author Michael Isvy
 * @see AbstractClinicServiceTests AbstractClinicServiceTests for more details. </p>
 */

@SpringBootTest
@ActiveProfiles({"spring-data-jpa", "hsqldb"})
class ClinicServiceSpringDataJpaTests extends AbstractClinicServiceTests {

}
