package wf.garnier.spring.boot.test.ch5.weather;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

// tag::class[]
class ModularityTests {

	// end::class[]
	// tag::test[]
	@Test
	void verifyStructure() {
		var modules = ApplicationModules.of(WeatherApplication.class);
		System.out.println(modules); <1>
		modules.verify(); <2>
	}
	// end::test[]

	@Test
	void writeDocumentation() {
		var modules = ApplicationModules.of(WeatherApplication.class);
		new Documenter(modules).writeDocumentation().writeModulesAsPlantUml();
	}
	// tag::class[]

}
// end::class[]
