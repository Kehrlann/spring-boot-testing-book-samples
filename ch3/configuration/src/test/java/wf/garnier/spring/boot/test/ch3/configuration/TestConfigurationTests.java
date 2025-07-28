package wf.garnier.spring.boot.test.ch3.configuration;

import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import wf.garnier.spring.boot.test.ch3.configuration.configurations.CustomTestConfiguration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests showcasing use of {@link TestConfiguration}.
 */
class TestConfigurationTests {

	@Nested
	@SpringBootTest
	class NestedConfigTests {

		@Test
		void things(@Autowired List<Thing> things) {
			assertThat(things).map(Thing::name)
				.containsExactlyInAnyOrder("bean-one", "bean-two", "nested-testconfig-bean",
						"configuration-test-package");
		}

		@Test
		void properties(@Autowired DemoProperties demoProperties) {
			assertThat(demoProperties.message()).isEqualTo("Hello, world!");
			assertThat(demoProperties.value()).isEqualTo(1);
		}

		@TestConfiguration
		static class TestConfig {

			@Bean
			Thing testConfigThing() {
				return new Thing("nested-testconfig-bean");
			}

		}

	}

	@Nested
	@SpringBootTest
	@Import(CustomTestConfiguration.class)
	class ImportConfigTests {

		@Test
		void things(@Autowired List<Thing> things) {
			assertThat(things).map(Thing::name)
				.containsExactlyInAnyOrder("bean-one", "bean-two", "configuration-test-package",
						"custom-testconfiguration");
		}

		@Test
		void properties(@Autowired DemoProperties demoProperties) {
			assertThat(demoProperties.message()).isEqualTo("Hello, world!");
			assertThat(demoProperties.value()).isEqualTo(1);
		}

	}

	@Nested
	@SpringBootTest
	class NoAdditionalConfigTests {

		@Test
		void things(@Autowired List<Thing> things) {
			assertThat(things).map(Thing::name)
				.containsExactlyInAnyOrder("bean-one", "bean-two", "configuration-test-package");
		}

		@Test
		void properties(@Autowired DemoProperties demoProperties) {
			assertThat(demoProperties.message()).isEqualTo("Hello, world!");
			assertThat(demoProperties.value()).isEqualTo(1);
		}

	}

}
