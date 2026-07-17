package wf.garnier.spring.boot.test.ch6.weather.preferences.internal;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import wf.garnier.spring.boot.test.ch6.weather.preferences.SortOrder;
import wf.garnier.spring.boot.test.ch6.weather.preferences.UnitSystem;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.TestPropertySources;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PreferencesConfigurationTests {

	@Nested
	@SpringBootTest(classes = { PreferencesConfiguration.class }, webEnvironment = SpringBootTest.WebEnvironment.NONE)
	class DefaultValues {

		@Autowired
		PreferencesProperties props;

		@Test
		void hasDefaults() {
			assertThat(props.getDefaults().darkMode()).isFalse();
			assertThat(props.getDefaults().units()).isEqualTo(UnitSystem.METRIC);
			assertThat(props.getDefaults().sortBy()).isEqualTo(SortOrder.ALPHABETICAL);
			assertThat(props.getTemperatureThreshold().cold()).isEqualTo(10);
			assertThat(props.getTemperatureThreshold().hot()).isEqualTo(25);
		}

	}

	@Nested
	@SpringBootTest(classes = { PreferencesConfiguration.class }, properties = { """
			preferences.defaults.dark-mode=true
			""" }, webEnvironment = SpringBootTest.WebEnvironment.NONE)
	class CustomPropertiesValue {

		@Autowired
		PreferencesProperties props;

		@Test
		void hasCustomValues() {
			assertThat(props.getDefaults().darkMode()).isTrue();
		}

	}

	@Nested
	@SpringBootTest(classes = { PreferencesConfiguration.class }, webEnvironment = SpringBootTest.WebEnvironment.NONE)
	@ActiveProfiles("dark-mode")
	class ProfileBasedValues {

		@Autowired
		PreferencesProperties props;

		@Test
		void hasCustomValues() {
			assertThat(props.getDefaults().darkMode()).isTrue();
		}

	}

	@Nested
	@SpringBootTest(classes = { PreferencesConfiguration.class }, webEnvironment = SpringBootTest.WebEnvironment.NONE)
	@TestPropertySource
	class FromTestPropertySource {

		@Autowired
		PreferencesProperties props;

		// properties are loaded from <test-package>/TestClass.properties
		// wf/garnier/spring/boot/test/ch6/weather/preferences/internal/PreferencesConfigurationTests$FromTestPropertySources.properties
		@Test
		void hasCustomValues() {
			assertThat(props.getDefaults().sortBy()).isEqualTo(SortOrder.DATE_ADDED);
		}

	}

	@Nested
	@SpringBootTest(classes = { PreferencesConfiguration.class }, webEnvironment = SpringBootTest.WebEnvironment.NONE)
	@TestPropertySource(value = "classpath:units-imperial.properties")
	class FromTestPropertySourceExplicit {

		@Autowired
		PreferencesProperties props;

		@Test
		void hasCustomValues() {
			assertThat(props.getDefaults().units()).isEqualTo(UnitSystem.IMPERIAL);
		}

	}

	@Nested
	@SpringBootTest(classes = { PreferencesConfiguration.class }, webEnvironment = SpringBootTest.WebEnvironment.NONE)
	@TestPropertySources({ @TestPropertySource(value = "classpath:units-imperial.properties"),
			@TestPropertySource(value = "classpath:application-dark-mode.properties") })
	class FromMultipleTestPropertySourceExplicit {

		@Autowired
		PreferencesProperties props;

		@Test
		void hasCustomValues() {
			assertThat(props.getDefaults().units()).isEqualTo(UnitSystem.IMPERIAL);
			assertThat(props.getDefaults().darkMode()).isTrue();
		}

	}

	@Nested
	@SpringBootTest(classes = { PreferencesConfiguration.class }, properties = { """
			preferences.defaults.dark-mode=true
			preferences.defaults.sort-by=date_added
			preferences.defaults.units=imperial
			preferences.temperature-threshold.cold=5
			preferences.temperature-threshold.hot=30
			""" }, webEnvironment = SpringBootTest.WebEnvironment.NONE)
	class CustomValues {

		@Autowired
		PreferencesProperties props;

		@Test
		void hasCustomValues() {
			assertThat(props.getDefaults().darkMode()).isTrue();
			assertThat(props.getDefaults().units()).isEqualTo(UnitSystem.IMPERIAL);
			assertThat(props.getDefaults().sortBy()).isEqualTo(SortOrder.DATE_ADDED);
			assertThat(props.getTemperatureThreshold().cold()).isEqualTo(5);
			assertThat(props.getTemperatureThreshold().hot()).isEqualTo(30);
		}

	}

	@Nested
	@Disabled // this would fail, the validation is wrong!
	@SpringBootTest(classes = { PreferencesConfiguration.class }, properties = { """
			preferences.temperature-threshold.cold=30
			preferences.temperature-threshold.hot=5
			""" }, webEnvironment = SpringBootTest.WebEnvironment.NONE)
	class InvalidValues {

		@Autowired
		PreferencesProperties props;

		@Test
		void hasInvalidValues() {
			assertThat(props.getTemperatureThreshold().cold()).isEqualTo(30);
			assertThat(props.getTemperatureThreshold().hot()).isEqualTo(5);
		}

	}

	@Nested
	class ManualSpringAppConstruction {

		@Test
		void defaults() {
			var builder = new SpringApplicationBuilder(PreferencesConfiguration.class).web(WebApplicationType.NONE);
			try (var applicationContext = builder.run()) {
				var props = applicationContext.getBean(PreferencesProperties.class);
				assertThat(props).isNotNull();
				assertThat(props.getDefaults().darkMode()).isFalse();
				assertThat(props.getDefaults().units()).isEqualTo(UnitSystem.METRIC);
				assertThat(props.getDefaults().sortBy()).isEqualTo(SortOrder.ALPHABETICAL);
				assertThat(props.getTemperatureThreshold().cold()).isEqualTo(10);
				assertThat(props.getTemperatureThreshold().hot()).isEqualTo(25);
			}
		}

		@Test
		void invalidThresholds() {
			var builder = new SpringApplicationBuilder(PreferencesConfiguration.class)
				.properties("preferences.temperature-threshold.hot=10", "preferences.temperature-threshold.cold=20")
				.web(WebApplicationType.NONE);

			assertThatThrownBy(builder::run).isInstanceOf(BeanCreationException.class)
				.rootCause()
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("preferences.temperature-threshold.hot (10.0) " + "must be higher than "
						+ "preferences.temperature-threshold.cold (20.0)");
		}

		@Test
		void environment() throws IOException {
			var env = envFromYaml("""
					preferences:
					  temperature-threshold:
					    hot: 10
					    cold: 20
					""");
			var builder = new SpringApplicationBuilder(PreferencesConfiguration.class).web(WebApplicationType.NONE)
				.environment(env);

			assertThatThrownBy(builder::run).isInstanceOf(BeanCreationException.class)
				.rootCause()
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("preferences.temperature-threshold.hot (10.0) " + "must be higher than "
						+ "preferences.temperature-threshold.cold (20.0)");
		}

		private static StandardEnvironment envFromYaml(String yamlProperties) throws IOException {
			var config = new ByteArrayResource(yamlProperties.getBytes(StandardCharsets.UTF_8));

			var propertySources = new YamlPropertySourceLoader().load("env-from-inline-test", config);
			var env = new StandardEnvironment();
			env.getPropertySources().addFirst(propertySources.getFirst());
			return env;
		}

	}

}
