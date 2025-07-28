package wf.garnier.spring.boot.test.ch3.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
class CustomTestConfiguration {

	@Bean
	Thing customThing() {
		return new Thing("custom-testconfiguration");
	}

}
