package wf.garnier.spring.boot.test.ch3.configuration.configurations;

import wf.garnier.spring.boot.test.ch3.configuration.Thing;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomConfiguration {

	@Bean
	Thing testSpecificThing() {
		return new Thing("configuration-test-package");
	}

}
