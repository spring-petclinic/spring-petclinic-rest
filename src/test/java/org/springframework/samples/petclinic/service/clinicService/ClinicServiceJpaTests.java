package org.springframework.samples.petclinic.service.clinicService;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * <p> Integration test using the jpa profile.
 *
 * @author Rod Johnson
 * @author Sam Brannen
 * @author Michael Isvy
 * @see AbstractClinicServiceTests AbstractClinicServiceTests for more details. </p>
 */

@SpringBootTest
@ActiveProfiles({"jpa", "hsqldb"})
class ClinicServiceJpaTests extends AbstractClinicServiceTests {

}
