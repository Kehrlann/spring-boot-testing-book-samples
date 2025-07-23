package wf.garnier.spring.boot.test.ch3.configuration;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
		classes = { ThingApplicationRunner.class, ThingConfiguration.class, CustomConfigTests.TestConfig.class })
class CustomConfigTests {

	@Test
	void things(@Autowired List<Thing> things) {
		assertThat(things).map(Thing::name)
			.containsExactlyInAnyOrder("bean-one", "bean-two", "test")
			// This is redundant with "containsExactly..."
			.doesNotContain("configuration-test-package");
	}

	@Test
	void properties(@Autowired Optional<DemoProperties> demoProperties) {
		assertThat(demoProperties).isEmpty();
	}

	@Configuration
	static class TestConfig {

		@Bean
		public Thing three() {
			return new Thing("test");
		}

	}

}
