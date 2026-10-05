package org.springframework.samples.petclinic.service.userService;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("hsqldb")
class UserServiceHsqlJdbcTests extends AbstractUserServiceTests {

}
