package org.springframework.samples.petclinic;

// import org.hsqldb.util.DatabaseManagerSwing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityFilterAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.OAuth2AutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
// @EnableAutoConfiguration(exclude={OAuth2AutoConfiguration.class,SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class})
public class PetClinicApplication extends SpringBootServletInitializer {
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(PetClinicApplication.class).headless(false);
	}

	public static void main(String[] args) {
		SpringApplication.run(PetClinicApplication.class, args);
//		System.setProperty("java.awt.headless", "false");
//		DatabaseManagerSwing.main(new String[] { "--url", "jdbc:hsqldb:mem:petclinic", "--user", "sa", "--password", "" });

	}

}
