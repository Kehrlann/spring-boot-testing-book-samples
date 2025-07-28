package wf.garnier.spring.boot.test.ch3.configuration;

import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import wf.garnier.spring.boot.test.ch3.configuration.configurations.CustomConfiguration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests showcasing the use of {@link Configuration} and {@code classes = {...}}.
 */
class CustomConfigTests {

	@Nested
	@SpringBootTest(classes = { ThingConfiguration.class })
	class CustomClassesTests {

		@Test
		void things(@Autowired List<Thing> things) {
			assertThat(things).map(Thing::name).containsOnly("bean-one", "bean-two");
		}

		@Test
		void properties(ApplicationContext applicationContext) {
			assertThat(applicationContext.getBeanNamesForType(DemoProperties.class)).isEmpty();
		}

		// This is NOT picked up, because of the explicit "classes = { ... }" parameter
		@Configuration
		static class TestConfig {

			@Bean
			public Thing three() {
				return new Thing("nested-test-bean");
			}

		}

	}

	@Nested
	@SpringBootTest
	class NestedConfigurationTests {

		// Only the nested "TestConfig" is picked up
		@Test
		void things(@Autowired List<Thing> things) {
			assertThat(things).map(Thing::name).containsOnly("nested-test-bean");
		}

		@Test
		void properties(ApplicationContext applicationContext) {
			assertThat(applicationContext.getBeanNamesForType(DemoProperties.class)).isEmpty();
		}

		@Configuration
		static class TestConfig {

			@Bean
			public Thing three() {
				return new Thing("nested-test-bean");
			}

		}

	}

	@Nested
	@SpringBootTest
	@Import(CustomConfiguration.class)
	class ImportConfiguration {

		// Only the "CustomConfiguration" + nested "TestConfig" are picked up
		@Test
		void things(@Autowired List<Thing> things) {
			assertThat(things).map(Thing::name).containsOnly("nested-test-bean", "configuration-test-package");
		}

		@Test
		void properties(ApplicationContext applicationContext) {
			assertThat(applicationContext.getBeanNamesForType(DemoProperties.class)).isEmpty();
		}

		@Configuration
		static class TestConfig {

			@Bean
			public Thing three() {
				return new Thing("nested-test-bean");
			}

		}

	}

}
