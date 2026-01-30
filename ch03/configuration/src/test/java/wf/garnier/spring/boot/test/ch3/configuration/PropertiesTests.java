package wf.garnier.spring.boot.test.ch3.configuration;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for application properties.
 */
class PropertiesTests {

	@Nested
	@SpringBootTest
	class DefaultConfigurationTests {

		@Autowired
		DemoProperties demoProperties;

		@Test
		void hasDefaultValues() {
			assertThat(demoProperties.message()).isEqualTo("Hello, world!");
			assertThat(demoProperties.value()).isEqualTo(1);
		}

	}

	@Nested
	// tag::properties-array[]
	@SpringBootTest(properties = { "demo.value=49", "demo.message=Hallo Welt" })
	class CustomPropertiesTests {

		// ... your test code ...
		// tag::ignored[]
		@Autowired
		DemoProperties demoProperties;

		@Test
		void hasCustomValue() {
			assertThat(demoProperties.message()).isEqualTo("Hallo Welt");
			assertThat(demoProperties.value()).isEqualTo(49);
		}
		// end::ignored[]

	}
	// end::properties-array[]

	@Nested
	// tag::properties-textblock[]
	@SpringBootTest(properties = { """
			demo.value=34
			demo.message=¡Hola, mundo!
			""" })
	class CustomPropertiesMultilineTests {

		// ... your test code ...
		// tag::ignored[]
		@Autowired
		DemoProperties demoProperties;

		@Test
		void hasCustomValue() {
			assertThat(demoProperties.message()).isEqualTo("¡Hola, mundo!");
			assertThat(demoProperties.value()).isEqualTo(34);
		}
		// end::ignored[]

	}
	// end::properties-textblock[]

	@Nested
	// tag::property-source[]
	@SpringBootTest
	@TestPropertySource(
			locations = "classpath:/custom-values.properties",
			encoding = "UTF-8"
	)
	class PropertySourceTests {

		// ... your test code ...
		// tag::ignored[]
		@Autowired
		DemoProperties demoProperties;

		@Test
		void hasCustomValue() {
			assertThat(demoProperties.message()).isEqualTo("ハロー・ワールド");
			assertThat(demoProperties.value()).isEqualTo(81);
		}
		// end::ignored[]

	}
	// end::property-source[]

	@Nested
	// tag::properties-profile[]
	@SpringBootTest
	@ActiveProfiles("test")
	class CustomProfileTests {

		// ... your test code ...
		// tag::ignored[]
		@Autowired
		DemoProperties demoProperties;

		@Test
		void hasCustomValue() {
			assertThat(demoProperties.message()).isEqualTo("Bonjour, monde!");
			assertThat(demoProperties.value()).isEqualTo(33);
		}
		// end::ignored[]

	}
	// end::properties-profile[]

}
