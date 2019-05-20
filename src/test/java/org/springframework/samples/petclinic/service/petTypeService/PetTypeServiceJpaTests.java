package org.springframework.samples.petclinic.service.petTypeService;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * <p> Integration test using the jpa profile.
 *
 * @author Rod Johnson
 * @author Sam Brannen
 * @author Michael Isvy
 * @see AbstractPetTypeServiceTests AbstractPetTypeServiceTests for more details. </p>
 */

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("jpa, hsqldb")
public class PetTypeServiceJpaTests extends AbstractPetTypeServiceTests {

}
