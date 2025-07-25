package wf.garnier.spring.boot.test.ch3.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
class ThingConfiguration {

	@Bean
	Thing one() {
		return new Thing("bean-one");
	}

	@Bean
	Thing two() {
		return new Thing("bean-two");
	}

	@Profile("custom")
	@Bean
	Thing withProfile() {
		return new Thing("bean-custom-profile");
	}

}
