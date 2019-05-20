package org.springframework.samples.petclinic.service.vetService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.service.VetService;
import org.springframework.samples.petclinic.util.EntityUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractVetServiceTests {

    @Autowired
    protected VetService vetService;

    @Test
    public void shouldFindVets() {
        Collection<Vet> vets = this.vetService.findVets();

        Vet vet = EntityUtils.getById(vets, Vet.class, 3);
        assertThat(vet.getLastName()).isEqualTo("Douglas");
        assertThat(vet.getNrOfSpecialties()).isEqualTo(2);
        assertThat(vet.getSpecialties().get(0).getName()).isEqualTo("dentistry");
        assertThat(vet.getSpecialties().get(1).getName()).isEqualTo("surgery");
    }

    @Test
    public void shouldFindVetDyId(){
        Vet vet = this.vetService.findVetById(1);
        assertThat(vet.getFirstName()).isEqualTo("James");
        assertThat(vet.getLastName()).isEqualTo("Carter");
    }

    @Test
    @Transactional
    public void shouldInsertVet() {
        Collection<Vet> vets = this.vetService.findAllVets();
        int found = vets.size();

        Vet vet = new Vet();
        vet.setFirstName("John");
        vet.setLastName("Dow");

        this.vetService.saveVet(vet);
        assertThat(vet.getId().longValue()).isNotEqualTo(0);

        vets = this.vetService.findAllVets();
        assertThat(vets.size()).isEqualTo(found + 1);
    }

    @Test
    @Transactional
    public void shouldUpdateVet(){
        Vet vet = this.vetService.findVetById(1);
        String oldLastName = vet.getLastName();
        String newLastName = oldLastName + "X";
        vet.setLastName(newLastName);
        this.vetService.saveVet(vet);
        vet = this.vetService.findVetById(1);
        assertThat(vet.getLastName()).isEqualTo(newLastName);
    }

    @Test
    @Transactional
    public void shouldDeleteVet(){
        Vet vet = this.vetService.findVetById(1);
        this.vetService.deleteVet(vet);
        try {
            vet = this.vetService.findVetById(1);
        } catch (Exception e) {
            vet = null;
        }
        assertThat(vet).isNull();
    }
}
