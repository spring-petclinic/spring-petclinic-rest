package org.springframework.samples.petclinic.service.clinicService;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * <p> Integration test using the 'Spring Data' profile.
 *
 * @author Michael Isvy
 * @see AbstractClinicServiceTests AbstractClinicServiceTests for more details. </p>
 */

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("spring-data-jpa, hsqldb")
public class ClinicServiceSpringDataJpaTests extends AbstractClinicServiceTests {

}
