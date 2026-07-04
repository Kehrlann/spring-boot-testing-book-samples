package wf.garnier.spring.boot.test.ch3.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
class ThingConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(ThingConfiguration.class);

	@Bean
	Thing redThing() {
		return new Thing("red");
	}

	@Bean
	Thing pinkThing(@Qualifier("redThing") Thing redThing) {
		logger.info("pink depends on {}", redThing);
		return new Thing("pink");
	}

	@Bean
	Thing greenThing(Gizmo gizmo, @Qualifier("blueWidget") Widget blueWidget) {
		logger.info("green depends on {}", gizmo);
		logger.info("green depends on {}", blueWidget);
		return new Thing("green");
	}

	@Profile("custom")
	@Bean
	Thing magentaThing() {
		return new Thing("magenta");
	}

}
