package wf.garnier.spring.boot.test.ch3.configuration.configurations;

import wf.garnier.spring.boot.test.ch3.configuration.Thing;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class CustomTestConfiguration {

	@Bean
	Thing customThing() {
		return new Thing("custom-testconfiguration");
	}

}
