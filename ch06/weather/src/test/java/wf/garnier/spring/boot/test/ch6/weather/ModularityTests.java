package wf.garnier.spring.boot.test.ch6.weather;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class ModularityTests {

	@Test
	void verifyStructure() {
		var modules = ApplicationModules.of(WeatherApplication.class);
		System.out.println(modules);
		modules.verify();
	}

	@Test
	void writeDocumentation() {
		var modules = ApplicationModules.of(WeatherApplication.class);
		new Documenter(modules).writeDocumentation().writeModulesAsPlantUml();
	}

}
