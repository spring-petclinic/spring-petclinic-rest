package org.springframework.samples.petclinic.service.userService;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles({"spring-data-jpa", "hsqldb"})
class UserServiceSpringDataJpaTests extends AbstractUserServiceTests {

}
