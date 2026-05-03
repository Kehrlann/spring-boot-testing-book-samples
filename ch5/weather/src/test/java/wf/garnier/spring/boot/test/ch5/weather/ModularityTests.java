package wf.garnier.spring.boot.test.ch5.weather;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class ModularityTests {

    @Test
	void writeDocumentation() {
        var modules = ApplicationModules.of(WeatherApplication.class);
        System.out.println(modules);
        new Documenter(modules).writeDocumentation().writeModulesAsPlantUml();
    }

    @Test
    void verifiesModularStructure() {
        ApplicationModules.of(WeatherApplication.class).verify();
    }

}
