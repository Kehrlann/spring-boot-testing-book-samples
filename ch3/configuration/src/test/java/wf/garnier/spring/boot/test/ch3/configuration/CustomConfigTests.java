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
import org.springframework.test.context.bean.override.convention.TestBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests showcasing the use of {@link Configuration} and {@code classes = {...}}.
 */
class CustomConfigTests {

	@Nested
	// tag::classes[]
	@SpringBootTest(classes = { ThingConfiguration.class }) // <1>
	class CustomClassesTests {

		// ... your test code ...
		// tag::ignored[]
		@MockitoBean
		Gizmo gizmo;

		@MockitoBean(name = "blueWidget")
		Widget blueWidget;

		@Test
		void things(@Autowired List<Thing> things) {
			assertThat(things).map(Thing::name).containsOnly("red", "pink", "green");
		}

		@Test
		void properties(ApplicationContext applicationContext) {
			assertThat(applicationContext.getBeanNamesForType(DemoProperties.class)).isEmpty();
		}
		// end::ignored[]

	}
	// end::classes[]

	@Nested
	@SpringBootTest(classes = { ThingConfiguration.class })
	class CustomClassesAndNestedTests {

		// ... your test code ...
		@MockitoBean
		Gizmo gizmo;

		@MockitoBean(name = "blueWidget")
		Widget blueWidget;

		@Test
		void things(@Autowired List<Thing> things) {
			assertThat(things).map(Thing::name).containsOnly("red", "pink", "green");
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
				return new Thing("silver");
			}

		}

	}

	@Nested
	@SpringBootTest
	class ReplaceBeanTests {

		@TestBean
		Thing greenThing;

		static Thing greenThing() {
			return new Thing("emerald");
		}

		@Test
		void things(@Autowired List<Thing> things) {
			assertThat(things).map(Thing::name).contains("emerald").doesNotContain("green");
		}

	}

	@Nested
	// tag::mockitobean-testbean[]
	@SpringBootTest(classes = { ThingConfiguration.class })
	class MockitoAndTestBeanTests {

		@MockitoBean(name = "blueWidget") // <1>
		Widget blueWidget;

		@TestBean
		Gizmo gizmo; // <2>

		static Gizmo gizmo() { // <2>
			return new Gizmo("test");
		}

		// ... your test code ...
		// tag::ignored[]
		@Test
		void contextLoads() {
		}
		// end::ignored[]

	}
	// end::mockitobean-testbean[]

	@Nested
	@SpringBootTest
	class NestedConfigurationTests {

		// Only the nested "TestConfig" is picked up
		@Test
		void things(@Autowired List<Thing> things) {
			assertThat(things).map(Thing::name).containsOnly("silver");
		}

		@Test
		void properties(ApplicationContext applicationContext) {
			assertThat(applicationContext.getBeanNamesForType(DemoProperties.class)).isEmpty();
		}

		@Configuration
		static class TestConfig {

			@Bean
			public Thing three() {
				return new Thing("silver");
			}

		}

	}

	@Nested
	@SpringBootTest
	@Import(CustomConfiguration.class)
	class NestedAndImportConfiguration {

		// The "CustomConfiguration" is added to the nested "TestConfig"
		@Test
		void things(@Autowired List<Thing> things) {
			assertThat(things).map(Thing::name).containsOnly("silver", "orange");
		}

		@Test
		void properties(ApplicationContext applicationContext) {
			assertThat(applicationContext.getBeanNamesForType(DemoProperties.class)).isEmpty();
		}

		@Configuration
		static class TestConfig {

			@Bean
			public Thing three() {
				return new Thing("silver");
			}

		}

	}

	@Nested
	@SpringBootTest
	@Import(CustomConfiguration.class)
	class ImportConfiguration {

		// The "CustomConfiguration" is added to the default component-scanned
		// configuration
		@Test
		void things(@Autowired List<Thing> things) {
			assertThat(things).map(Thing::name).containsOnly("red", "pink", "green", "orange");
		}

		@Test
		void properties(@Autowired DemoProperties demoProperties) {
			assertThat(demoProperties.message()).isEqualTo("Hello, world!");
			assertThat(demoProperties.value()).isEqualTo(1);
		}

	}

}
