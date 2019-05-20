/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.service.petTypeService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.*;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.samples.petclinic.service.PetTypeService;
import org.springframework.samples.petclinic.util.EntityUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p> Base class for {@link ClinicService} integration tests. </p> <p> Subclasses should specify Spring context
 * configuration using {@link ContextConfiguration @ContextConfiguration} annotation </p> <p>
 * AbstractclinicServiceTests and its subclasses benefit from the following services provided by the Spring
 * TestContext Framework: </p> <ul> <li><strong>Spring IoC container caching</strong> which spares us unnecessary set up
 * time between test execution.</li> <li><strong>Dependency Injection</strong> of test fixture instances, meaning that
 * we don't need to perform application context lookups. See the use of {@link Autowired @Autowired} on the <code>{@link
 * AbstractPetTypeServiceTests#petTypeService petTypeService}</code> instance variable, which uses autowiring <em>by
 * type</em>. <li><strong>Transaction management</strong>, meaning each test method is executed in its own transaction,
 * which is automatically rolled back by default. Thus, even if tests insert or otherwise change database state, there
 * is no need for a teardown or cleanup script. <li> An {@link org.springframework.context.ApplicationContext
 * ApplicationContext} is also inherited and can be used for explicit bean lookup if necessary. </li> </ul>
 *
 * @author Ken Krebs
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Michael Isvy
 * @author Vitaliy Fedoriv
 */
public abstract class AbstractPetTypeServiceTests {

    @Autowired
    protected PetTypeService petTypeService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void shouldFindPetTypeById(){
    	PetType petType = this.petTypeService.findPetTypeById(1);
    	assertThat(petType.getName()).isEqualTo("cat");
    }

    @Test
    public void shouldFindAllPetTypes(){
        Collection<PetType> petTypes = this.petTypeService.findAllPetTypes();
        PetType petType1 = EntityUtils.getById(petTypes, PetType.class, 1);
        assertThat(petType1.getName()).isEqualTo("cat");
        PetType petType3 = EntityUtils.getById(petTypes, PetType.class, 3);
        assertThat(petType3.getName()).isEqualTo("lizard");
    }

    @Test
    @Transactional
    public void shouldInsertPetType() {
        Collection<PetType> petTypes = this.petTypeService.findAllPetTypes();
        int found = petTypes.size();

        PetType petType = new PetType();
        petType.setName("tiger");

        this.petTypeService.savePetType(petType);
        assertThat(petType.getId().longValue()).isNotEqualTo(0);

        petTypes = this.petTypeService.findAllPetTypes();
        assertThat(petTypes.size()).isEqualTo(found + 1);
    }

    @Test
    @Transactional
    public void shouldUpdatePetType(){
    	PetType petType = this.petTypeService.findPetTypeById(1);
    	String oldLastName = petType.getName();
        String newLastName = oldLastName + "X";
        petType.setName(newLastName);
        this.petTypeService.savePetType(petType);
        petType = this.petTypeService.findPetTypeById(1);
        assertThat(petType.getName()).isEqualTo(newLastName);
    }

    @Test
    @Transactional
    public void shouldDeletePetType(){
    	PetType petType = this.petTypeService.findPetTypeById(1);
        this.petTypeService.deletePetType(petType);
        try {
        	petType = this.petTypeService.findPetTypeById(1);
		} catch (Exception e) {
			petType = null;
		}
        assertThat(petType).isNull();
    }
}
